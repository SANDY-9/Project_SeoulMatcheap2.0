package com.sandy.seoul_matcheap.data.store

import com.sandy.seoul_matcheap.data.store.entity.MenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-04-14
 * @desc
 */
interface SeoulOpenAPI {

    @GET("{id}")
    suspend fun getMenu(
        @Path("id")id: String
    ) : Response<MenuResponse>

}