package com.jerry.ronaldo.siufascore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.data.source.FirebaseAuthDataSource
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteTeamsRepository
import com.jerry.ronaldo.siufascore.utils.FavoriteTeamException
import com.jerry.ronaldo.siufascore.utils.IODispatcher
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

class FavoriteTeamsRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseAuthDataSource,
    private val firestore: FirebaseFirestore,
    @IODispatcher private val ioDispatcher:CoroutineDispatcher
) : FavoriteTeamsRepository {
    companion object {
        private const val COLLECTION_FAVORITE_TEAMS = "favorite_teams"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_TEAM_ID = "teamId"
        private const val FIELD_ADDED_TIMESTAMP = "addedTimestamp"
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
                throw FavoriteTeamException.TeamAlreadyFavorite
            }
            val favoriteTeam = FavoriteTeam(
                addedTimestamp = System.currentTimeMillis(),
                userId = userId,
                team = team,
                league = league
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
                throw FavoriteTeamException.TeamNotFound
            }
            docRef.delete().await()
        }
    }

    override suspend fun getFavoriteTeams(): Result<List<FavoriteTeam>> {
        return executedWithUserCheck { userId ->
            val snapshot = firestore.collection(COLLECTION_FAVORITE_TEAMS)
                .whereEqualTo(FIELD_USER_ID, userId)
                .orderBy(FIELD_ADDED_TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(FavoriteTeam::class.java)
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
                    close(error)
                    return@addSnapshotListener
                }

                val favoriteTeams = snapshot?.toObjects(FavoriteTeam::class.java)
                    ?: emptyList()
                trySend(favoriteTeams)
            }

        awaitClose { listener.remove() }
    }.catch { error ->
        emit(emptyList())
    }.flowOn(ioDispatcher)

    private suspend fun <T> executedWithUserCheck(action: suspend (String) -> T): Result<T> {
        return try {
            val currentUserId =
                currentUserId.firstOrNull() ?: throw FavoriteTeamException.UserNotLoggedIn
            val result = action(currentUserId)
            Result.success(result)
        } catch (e: FavoriteTeamException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(FavoriteTeamException.FirestoreError(e))
        }
    }
}