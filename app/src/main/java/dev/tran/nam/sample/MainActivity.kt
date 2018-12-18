package dev.tran.nam.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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

    private lateinit var retrofit: Retrofit
    lateinit var adapter: ArticleAdapter
    private var position = -1

    private val rv: LimitRecyclerView by lazy {
        findViewById<LimitRecyclerView>(R.id.rv_item)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
            .baseUrl("http://192.168.7.152:5000/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setLenient()
                        .create()
                )
            )
            .build()

        if (savedInstanceState == null) {
            callApi(0)
        } else {
            position = savedInstanceState.getInt("position", -1)
            val data: ArrayList<ArticleModel>? = savedInstanceState.getParcelableArrayList("listArticle")
            data?.let {
                adapter.add(it,true)
                if (position != -1){
                    rv.layoutManager?.scrollToPosition(position)
                    position = -1
                }
            }
        }
    }

    override fun onLoadBefore(firstItem: Any?) {
        firstItem?.let {
            if (it is ArticleModel) {
                callApi(1, idBefore = it.id)
            }
        }
    }

    override fun onLoadMore(lastItem: Any?) {
        lastItem?.let {
            if (it is ArticleModel) {
                callApi(2, idAfter = it.id)
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
                    adapter.updateError(t.message) {
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
                        adapter.updateError("Error") {
                            callApi(type, idBefore, idAfter)
                        }
                    }
                }
            })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelableArrayList("listArticle", adapter.getData())
        val layoutManager = rv.layoutManager
        layoutManager?.let {
            val position : Int = when (layoutManager) {
                is GridLayoutManager -> layoutManager.findFirstCompletelyVisibleItemPosition()
                is StaggeredGridLayoutManager -> {
                    layoutManager.findFirstVisibleItemPositions(null)[0]
                }
                else -> (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            }
            outState?.putInt("position", position)
        }
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
