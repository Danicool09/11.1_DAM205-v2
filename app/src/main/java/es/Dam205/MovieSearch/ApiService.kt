package es.Dam205.MovieSearch

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ApiService {

    interface IMDbApi {

        @GET("/en/API/SearchMovie/{apiKey}/{expression}")
        fun getMovies(@Path("apiKey") apiKey: String, @Path("expression") expression: String):
                Call<MoviesResponse>
    }


    data class Movie(val title: String, val description: String, val image: String)


    data class MoviesResponse(val results: ArrayList<ApiService.Movie>)
}