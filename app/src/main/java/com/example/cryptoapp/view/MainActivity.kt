package com.example.cryptoapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.adapter.RecyclerViewAdapter
import com.example.cryptoapp.databinding.ActivityMainBinding
import com.example.cryptoapp.model.CryptoModel
import com.example.cryptoapp.service.CryptoAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val BASE_URL = "https://raw.githubusercontent.com/"
private var cryptoModels: ArrayList<CryptoModel>? = null
private lateinit var binding: ActivityMainBinding
private var recyclerViewAdapter : RecyclerViewAdapter? = null
private var compositeDisposable : CompositeDisposable? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        compositeDisposable = CompositeDisposable()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager


        loadData()
    }


    private fun loadData() {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //creating object
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)  //inflating with service

        compositeDisposable?.add(
            retrofit.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )

    }

    private fun handleResponse(cryptoList: List<CryptoModel>) {
        cryptoModels = ArrayList(cryptoList)

        cryptoModels?.let {
            recyclerViewAdapter = RecyclerViewAdapter(it,this)
            binding.recyclerView.adapter = recyclerViewAdapter
        }
    }

    fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this, "Currency for this is : ${cryptoModel.currency}", Toast.LENGTH_LONG)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable?.clear()
    }


}
