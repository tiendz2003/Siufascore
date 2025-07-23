package com.jerry.ronaldo.siufascore.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.getValue
import com.jerry.ronaldo.siufascore.data.model.CommentResult
import com.jerry.ronaldo.siufascore.data.model.LiveComment
import com.jerry.ronaldo.siufascore.data.model.StreamResponse
import com.jerry.ronaldo.siufascore.data.remote.LiveStreamingApiService
import com.jerry.ronaldo.siufascore.domain.model.User
import com.jerry.ronaldo.siufascore.domain.repository.LiveStreamRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import com.jerry.ronaldo.siufascore.utils.initDatabaseListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveStreamRepositoryImpl @Inject constructor(
    private val liveStreamApiService: LiveStreamingApiService,
    private val database: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : LiveStreamRepository {
    companion object {
        private const val COMMENTS_PATH = "comments"
        private const val COMMENT_COUNT_PATH = "comment_counts"
        private const val TAG = "CommentsRepository"
    }

    override suspend fun getLiveStreamById(id: Long): Resource<StreamResponse> {
        return try {
            val response = liveStreamApiService.getStreamUrl(
                matchId = id
            )
            if (response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(
                    Exception("Không có dữ liệu livestream cho trận đấu này.")
                )
            }
        } catch (e: Exception) {
            Resource.Error(
                e.handleException()
            )
        }
    }

    override fun getComments(matchId: String, limit: Int): Flow<Resource<List<LiveComment>>> =
        callbackFlow {
            trySend(Resource.Loading)
            val comments = mutableListOf<LiveComment>()
            val listener = database.reference.child(COMMENTS_PATH)
                .child(matchId)
                .orderByChild("timestamp")
                .limitToLast(limit)
                .initDatabaseListener(
                    onChildAdded = { snapshot, _ ->
                        try {
                            val comment =
                                snapshot.getValue<LiveComment>()?.copy(id = snapshot.key ?: "")
                            comment?.let {
                                // Insert in correct position based on timestamp
                                val insertIndex =
                                    comments.binarySearchBy(it.timestamp) { comment -> comment.timestamp }
                                val index = if (insertIndex < 0) -(insertIndex + 1) else insertIndex
                                comments.add(index, it)

                                trySend(Resource.Success(comments.toList()))
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Không thể lấy comment  ")
                            trySend(Resource.Error(e))
                        }
                    },
                    onChildChanged = { snapshot, _ ->
                        try {
                            val updatedComment =
                                snapshot.getValue<LiveComment>()?.copy(id = snapshot.key ?: "")
                            updatedComment?.let { updated ->
                                val index = comments.indexOfFirst { it.id == updated.id }
                                if (index != -1) {
                                    comments[index] = updated
                                    trySend(Resource.Success(comments.toList()))
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Không thể lấy comment")
                            trySend(Resource.Error(e))
                        }
                    },
                    onChildRemoved = { snapshot ->
                        try {
                            val commentId = snapshot.key
                            commentId?.let { id ->
                                comments.removeAll { it.id == id }
                                trySend(Resource.Success(comments.toList()))
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error removing comment")
                            trySend(Resource.Error(e))
                        }
                    },
                    onCancelled = { error ->
                        trySend(Resource.Error(error.toException()))
                        close(error.toException())
                    }
                )
            awaitClose {
                Timber.d("Đóng kết nối lắng nghe bình luận cho trận đấu $matchId")
                database.reference.child(COMMENTS_PATH)
                    .child(matchId)
                    .removeEventListener(listener)
            }
        }

    override fun getCommentsWithPagination(
        matchId: String,
        limit: Int,
        startAfterKey: String?
    ): Flow<Resource<CommentResult>> = callbackFlow {
        trySend(Resource.Loading)
        var query = database.reference.child(
            COMMENTS_PATH
        ).child(matchId)
            .orderByChild("timestamp")
            .limitToLast(limit + 1)
        startAfterKey?.let { key ->
            query = query.endBefore(null, key)
        }
        query.get()
            .addOnSuccessListener { snapshot ->
                try {
                    val comments = mutableListOf<LiveComment>()
                    val keys = mutableListOf<String>()
                    snapshot.children.forEach { child ->
                        val comment = child.getValue<LiveComment>()?.copy(id = child.key ?: "")
                        comment?.let {
                            comments.add(it)
                            keys.add(child.key ?: "")
                        }
                    }
                    val hasMore = comments.size > limit
                    val currentComment = if (hasMore) {
                        comments.dropLast(1)
                    } else {
                        comments
                    }
                    val lastKey = if (hasMore) keys.getOrNull(keys.size - 2) else null
                    val result = CommentResult(
                        comments = currentComment,
                        hasMore = hasMore,
                        lastKey = lastKey
                    )
                    trySend(Resource.Success(result))
                } catch (e: Exception) {
                    Timber.e(e, "Lỗi khi tải cmt")
                    trySend(Resource.Error(e))
                }
            }.addOnFailureListener { e ->
                Timber.e(e, "Lỗi khi tải cmt")
                trySend(Resource.Error(e))
            }
        awaitClose {
            Timber.d("Đóng kết nối lắng nghe bình luận với phân trang cho trận đấu $matchId")
        }
    }

    override suspend fun postComment(matchId: String, comment: LiveComment): Resource<String> {
        return try {
            if (firebaseAuth.currentUser == null) {
                Timber.e("Người dùng chưa đăng nhập")
                return Resource.Error(Exception("Bạn cần đăng nhập để bình luận"))
            }
            Timber.d("Đang đăng bình luận cho trận đấu $matchId: ${comment.comment}")
            val cmtRef = database.reference.child(COMMENTS_PATH).child(matchId).push()
            val cmt = comment.copy(
                id = cmtRef.key ?: "",
                userId = firebaseAuth.currentUser?.uid ?: "",
                userName = firebaseAuth.currentUser?.displayName ?: "Khách",
                userImage = firebaseAuth.currentUser?.photoUrl?.toString() ?: ""
            )
            val updates = hashMapOf(
                "$COMMENTS_PATH/$matchId/${cmtRef.key}" to cmt,
                "$COMMENT_COUNT_PATH/$matchId" to ServerValue.increment(1)
            )
            database.reference.updateChildren(updates).await()
            Timber.d("Đã đăng bình luận: ${cmt.comment} với ID: ${cmt.id}")
            Resource.Success(cmtRef.key ?: "")
        } catch (e: Exception) {
            Timber.e(e, "Lỗi khi đăng bình luận")
            Resource.Error(e.handleException())
        }
    }

}