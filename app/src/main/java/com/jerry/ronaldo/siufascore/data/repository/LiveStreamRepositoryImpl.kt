package com.jerry.ronaldo.siufascore.data.repository

import com.jerry.ronaldo.siufascore.data.model.StreamResponse
import com.jerry.ronaldo.siufascore.data.remote.LiveStreamingApiService
import com.jerry.ronaldo.siufascore.domain.repository.LiveStreamRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveStreamRepositoryImpl @Inject constructor(
    private val liveStreamApiService: LiveStreamingApiService
) : LiveStreamRepository {
    /*override suspend fun getLiveStreams(): Resource<List<LiveMatch>> {
        return try {

        }catch (e:Exception){
            Resource.Error(
                e.handleException()
            )
        }
    }*/

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

}