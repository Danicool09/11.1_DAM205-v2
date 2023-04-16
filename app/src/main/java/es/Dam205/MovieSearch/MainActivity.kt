package es.Dam205.MovieSearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*



class MainActivity : AppCompatActivity() {
    private val imdbBaseUrl = "https://imdb-api.com"
    private val apiKey = "k_fs5ge7r2"


    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val imdbService = retrofit.create(ApiService.IMDbApi::class.java)

    private val movies = ArrayList<ApiService.Movie>()
    private val adapter = MoviesAdapter()

    private lateinit var searchButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var moviesList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        searchButton = findViewById(R.id.searchButton)
        queryInput = findViewById(R.id.queryInput)
        moviesList = findViewById(R.id.movies)

        adapter.movies = movies

        moviesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        moviesList.adapter = adapter

        searchButton.setOnClickListener {
            if (queryInput.text.isNotEmpty()) {
                searchMovies()
            }
        }
    }


    private fun searchMovies() {
        imdbService.getMovies(apiKey, queryInput.text.toString())
            .enqueue(object : Callback<ApiService.MoviesResponse> {
                override fun onResponse(
                    call: Call<ApiService.MoviesResponse>,
                    response: Response<ApiService.MoviesResponse>
                ) {
                    when (response.code()) {

                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                movies.clear()
                                movies.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", "")
                            } else {
                                showMessage(getString(R.string.nothing_found), "No has introducido nada")
                            }
                        }
                        else -> {
                            showMessage(getString(R.string.something_went_wrong), response.code().toString())
                        }
                    }
                }

                override fun onFailure(call: Call<ApiService.MoviesResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                }

            })
    }

    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            movies.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }
}



class MovieViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_list_item, parent, false)
    ) {

    private val title: TextView = itemView.findViewById(R.id.title)
    private val description: TextView = itemView.findViewById(R.id.description)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)

    fun bind(movie: ApiService.Movie) {
        Glide.with(itemView.context)
            .load(movie.image)
            .placeholder(R.drawable.no_artwork)
            .centerInside()
            .into(artwork)

        title.text = movie.title
        description.text = movie.description
    }
}



