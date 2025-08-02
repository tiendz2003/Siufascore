package com.jerry.ronaldo.siufascore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jerry.ronaldo.siufascore.data.model.FavoritePlayer
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.data.source.FirebaseAuthDataSource
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteRepository
import com.jerry.ronaldo.siufascore.utils.FavoriteException
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseAuthDataSource,
    private val firestore: FirebaseFirestore,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : FavoriteRepository {
    companion object {
        private const val COLLECTION_FAVORITE_TEAMS = "favorite_teams"
        private const val COLLECTION_FAVORITE_PLAYERS = "favorite_players"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_TEAM_ID = "teamId"
        private const val FIELD_ADDED_TIMESTAMP = "addedTimestamp"
        private const val FIELD_IS_ENABLE_NOTIFICATION = "enableNotification"
    }

    private val currentUserId: Flow<String?> = firebaseDataSource.currentUser.map { it?.id }

    override suspend fun addFavoriteTeam(team: TeamInfo, league: LeagueInfo): Result<Unit> {
        return executedWithUserCheck { userId ->
            Timber.tag("FavoriteTeamsRepositoryImpl").d("Adding favorite team: $team")
            val documentId = "${userId}_${team.id}"
            val batch = firestore.batch()
            val docRef = firestore.collection(COLLECTION_FAVORITE_TEAMS).document(documentId)
            val existingDoc = firestore.collection(COLLECTION_FAVORITE_TEAMS)
                .document(documentId)
                .get()
                .await()
            if (existingDoc.exists()) {
                throw FavoriteException.TeamAlreadyFavorite
            }
            val favoriteTeam = FavoriteTeam(
                addedTimestamp = System.currentTimeMillis(),
                userId = userId,
                team = team,
                league = league,
                enableNotification = false
            )

            batch.set(docRef, favoriteTeam)
            batch.commit().await()
        }
    }

    override suspend fun removeFavoriteTeam(teamId: Int): Result<Unit> {
        return executedWithUserCheck { userId ->
            // Lấy đúng document ID đã tạo trước đó
            val documentId = "${userId}_${teamId}"
            val docRef = firestore
                .collection(COLLECTION_FAVORITE_TEAMS)
                .document(documentId)

            // Xóa document
            val document = docRef.get().await()
            if (!document.exists()) {
                throw FavoriteException.TeamNotFound
            }
            docRef.delete().await()
        }
    }

    override suspend fun toggleNotification(teamId: Int, isEnabled: Boolean): Result<Unit> {
        return executedWithUserCheck { userId ->
            Timber.tag("FavoriteTeamsRepositoryImpl")
                .d("Toggling notification for team $teamId to $isEnabled")

            val documentId = "${userId}_${teamId}"
            val docRef = firestore.collection(COLLECTION_FAVORITE_TEAMS).document(documentId)

            // Kiểm tra document tồn tại
            val document = docRef.get().await()
            if (!document.exists()) {
                throw FavoriteException.TeamNotFound
            }

            // Update chỉ field isEnableNotification
            val updates = mapOf(FIELD_IS_ENABLE_NOTIFICATION to isEnabled)
            docRef.update(updates).await()

            Timber.tag("FavoriteTeamsRepositoryImpl")
                .d("Successfully updated notification status for team $teamId")
        }
    }

    override suspend fun isFavoriteTeam(teamId: Int): Result<Boolean> {
        return try {
            val userId = currentUserId.firstOrNull()
                ?: return Result.success(false) // Nếu chưa đăng nhập, chắc chắn là không phải

            val documentId = "${userId}_${teamId}"
            val document = firestore.collection(COLLECTION_FAVORITE_TEAMS)
                .document(documentId)
                .get()
                .await()

            // Trả về true nếu document tồn tại, ngược lại trả về false
            Result.success(document.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeFavoriteTeams(): Flow<List<FavoriteTeam>> = callbackFlow {
        val userId = currentUserId.firstOrNull()

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_FAVORITE_TEAMS)
            .whereEqualTo(FIELD_USER_ID, userId)
            .orderBy(FIELD_ADDED_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.tag("FavoriteTeamsRepositoryImpl")
                        .e(error, "Error fetching favorite teams")
                    close(error)
                    return@addSnapshotListener
                }

                val favoriteTeams = snapshot?.toObjects(FavoriteTeam::class.java)
                    ?: emptyList()
                trySend(favoriteTeams)
            }

        awaitClose { listener.remove() }
    }.catch {
        emit(emptyList())
    }.flowOn(ioDispatcher)

    override suspend fun addFavoritePlayer(player: FavoritePlayer): Result<Unit> {
        return executedWithUserCheck { userId ->
            Timber.tag("FavoriteRepositoryImpl").d("Adding favorite player: ${player.playerName}")

            val documentId = "${userId}_${player.playerId}"
            val docRef = firestore.collection(COLLECTION_FAVORITE_PLAYERS).document(documentId)

            // Check if player is already in favorites
            val existingDoc = docRef.get().await()
            if (existingDoc.exists()) {
                throw FavoriteException.PlayerAlreadyFavorite
            }

            // Create favorite player with user info
            val favoritePlayer = player.copy(
                userId = userId,
                addedTimestamp = System.currentTimeMillis(),
                enableNotification = false
            )

            // Save to Firestore
            docRef.set(favoritePlayer).await()

            Timber.tag("FavoriteRepositoryImpl")
                .d("Successfully added favorite player: ${player.playerName}")
        }
    }

    override suspend fun removeFavoritePlayer(playerId: String): Result<Unit> {
        return executedWithUserCheck { userId ->
            Timber.tag("FavoriteRepositoryImpl").d("Removing favorite player: $playerId")

            val documentId = "${userId}_${playerId}"
            val docRef = firestore.collection(COLLECTION_FAVORITE_PLAYERS).document(documentId)

            // Check if document exists
            val document = docRef.get().await()
            if (!document.exists()) {
                throw FavoriteException.PlayerNotFound
            }

            // Delete document
            docRef.delete().await()

            Timber.tag("FavoriteRepositoryImpl")
                .d("Successfully removed favorite player: $playerId")
        }
    }

    override suspend fun isFavoritePlayer(playerId: String): Result<Boolean> {
        return try {
            val userId = currentUserId.firstOrNull()
                ?: return Result.success(false)

            val documentId = "${userId}_${playerId}"
            val document = firestore.collection(COLLECTION_FAVORITE_PLAYERS)
                .document(documentId)
                .get()
                .await()

            Result.success(document.exists())
        } catch (e: Exception) {
            Timber.tag("FavoriteRepositoryImpl")
                .e(e, "Error checking if player is favorite: $playerId")
            Result.failure(e)
        }
    }

    override suspend fun togglePlayerNotification(
        playerId: String,
        isEnabled: Boolean
    ): Result<Unit> {
        return executedWithUserCheck { userId ->
            Timber.tag("FavoriteRepositoryImpl")
                .d("Toggling notification for player $playerId to $isEnabled")

            val documentId = "${userId}_${playerId}"
            val docRef = firestore.collection(COLLECTION_FAVORITE_PLAYERS).document(documentId)

            // Check if document exists
            val document = docRef.get().await()
            if (!document.exists()) {
                throw FavoriteException.PlayerNotFound
            }

            // Update notification setting
            val updates = mapOf(FIELD_IS_ENABLE_NOTIFICATION to isEnabled)
            docRef.update(updates).await()

            Timber.tag("FavoriteRepositoryImpl")
                .d("Successfully updated notification status for player $playerId")
        }
    }

    override fun observeFavoritePlayers(): Flow<Resource<List<FavoritePlayer>>> = callbackFlow {
        val userId = currentUserId.firstOrNull()
        trySend(Resource.Loading)
        if (userId == null) {
            trySend(Resource.Success(emptyList()))
            close()
            return@callbackFlow
        }

        val listener = firestore.collection(COLLECTION_FAVORITE_PLAYERS)
            .whereEqualTo(FIELD_USER_ID, userId)
            .orderBy(FIELD_ADDED_TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.tag("FavoriteRepositoryImpl")
                        .e(error, "Error fetching favorite players")
                    close(error)
                    return@addSnapshotListener
                }

                val favoritePlayers = snapshot?.toObjects(FavoritePlayer::class.java)
                    ?: emptyList()

                Timber.tag("FavoriteRepositoryImpl")
                    .d("Observed ${favoritePlayers.size} favorite players")

                trySend(Resource.Success(favoritePlayers))
            }

        awaitClose { listener.remove() }
    }.catch { exception ->
        Timber.tag("FavoriteRepositoryImpl")
            .e(exception, "Error in observeFavoritePlayers flow")
        emit(Resource.Success(emptyList()))
    }.flowOn(ioDispatcher)

    private suspend fun <T> executedWithUserCheck(action: suspend (String) -> T): Result<T> {
        return try {
            val currentUserId =
                currentUserId.firstOrNull() ?: throw FavoriteException.UserNotLoggedIn
            val result = action(currentUserId)
            Result.success(result)
        } catch (e: FavoriteException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(FavoriteException.FirestoreError(e))
        }
    }
}