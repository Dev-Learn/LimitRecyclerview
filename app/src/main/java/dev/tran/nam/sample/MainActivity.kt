package dev.tran.nam.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import dev.tran.nam.library.LimitRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity(), LimitRecyclerView.OnLoadListener<Any> {

    lateinit var retrofit: Retrofit
    lateinit var adapter: ArticleAdapter

    private val rv: LimitRecyclerView by lazy {
        findViewById<LimitRecyclerView>(R.id.rv_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        adapter = ArticleAdapter()

        rv.adapter = adapter

        rv.setOnLoadListener(this)

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.5.253:5000/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()
        callApi(0)
    }

    override fun onLoadBefore(firstItem: Any?) {
        firstItem?.let {
            if (it is ArticleModel) {
                callApi(1,idBefore = it.id)
            }
        }
    }

    override fun onLoadMore(lastItem: Any?) {
        lastItem?.let {
            if (it is ArticleModel) {
                callApi(2,idAfter = it.id)
            }
        }
    }

    fun callApi(type: Int, idBefore: Int? = null, idAfter: Int? = null) {
        val limit = when (type) {
            0 -> 50
            else -> 20
        }

        retrofit.create(ArticleAPI::class.java).getArticle(before = idBefore, after = idAfter, limit = limit)
            .enqueue(object : Callback<List<ArticleModel>> {
                override fun onFailure(call: Call<List<ArticleModel>>, t: Throwable) {
                    adapter.updateError(t.message){
                        callApi(type, idBefore, idAfter)
                    }
                }

                override fun onResponse(call: Call<List<ArticleModel>>, response: Response<List<ArticleModel>>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            adapter.updateData(data = result, isInitial = type == 0)
                        }
                    } else {
                        adapter.updateError("Error"){
                            callApi(type, idBefore, idAfter)
                        }
                    }
                }
            })
    }
}

internal interface ArticleAPI {

    @GET("/getArticle")
    fun getArticle(
        @Query("before") before: Int? = null, @Query("after") after: Int? = null, @Query(
            "limit"
        ) limit: Int = 20
    ): Call<List<ArticleModel>>
}
