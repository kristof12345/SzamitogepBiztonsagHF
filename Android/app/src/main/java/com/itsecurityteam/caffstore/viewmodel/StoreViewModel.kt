package com.itsecurityteam.caffstore.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.model.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDateTime
import kotlin.random.Random

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    private val caffs = MutableLiveData<List<Caff>>()
    val Caffs: LiveData<List<Caff>>
        get() = caffs

    private val selectedCaff = MutableLiveData<Caff>()
    val SelectedCaff: LiveData<Caff>
        get() = selectedCaff

    private val comments = MutableLiveData<List<Comment>>()
    val Comments: LiveData<List<Comment>>
        get() = comments

    init {
        viewModelScope.launch {
            val urls = List(6) {
                when (it) {
                    0 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d90umas-a4039828-7251-4213-9e63-34b661233ab1.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMWY5MzYzYzctZDVjNC00Y2VlLTg2NGMtY2MyOTBmNTVhNDEzXC9kOTB1bWFzLWE0MDM5ODI4LTcyNTEtNDIxMy05ZTYzLTM0YjY2MTIzM2FiMS5qcGcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ._Clp7yyi-CtQqLUS2Jy7Ni-_aQ6jTdBM7mfI_EMeO24"
                    1 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d90kh0r-65246c72-073e-498e-9aee-6e7599ac599c.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMWY5MzYzYzctZDVjNC00Y2VlLTg2NGMtY2MyOTBmNTVhNDEzXC9kOTBraDByLTY1MjQ2YzcyLTA3M2UtNDk4ZS05YWVlLTZlNzU5OWFjNTk5Yy5qcGcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.DvLC2bV70roNlYYZaMmJG2KZNRH9-cuVyydQdd503Vg"
                    2 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/363b7cf2-e398-444b-b4ef-acd6cbf89d58/dcvlgyh-fe924661-85de-4884-acc1-dd9c69430bde.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMzYzYjdjZjItZTM5OC00NDRiLWI0ZWYtYWNkNmNiZjg5ZDU4XC9kY3ZsZ3loLWZlOTI0NjYxLTg1ZGUtNDg4NC1hY2MxLWRkOWM2OTQzMGJkZS5qcGcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.IWYjkt2p6d-GsgxfqAEgNPD_DkDTfro77S8ltf4y-Mg"
                    //2 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d8l0sr0-0cb73387-be6b-44a1-b196-4a365625012a.jpg/v1/fill/w_600,h_849,q_75,strp/tifa_lockhart___adrian_wolve_by_adrianwolve_d8l0sr0-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD04NDkiLCJwYXRoIjoiXC9mXC8xZjkzNjNjNy1kNWM0LTRjZWUtODY0Yy1jYzI5MGY1NWE0MTNcL2Q4bDBzcjAtMGNiNzMzODctYmU2Yi00NGExLWIxOTYtNGEzNjU2MjUwMTJhLmpwZyIsIndpZHRoIjoiPD02MDAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.-Fes0oC0daRCbdXTVF-0nwA8pdg7H1oRPM0Ffw2OAps"
                    3 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/0a6c753f-33d1-4fd3-99c5-29a898154388/d8u7nfs-d1e371d0-9cde-4c92-8223-239eab2a372f.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMGE2Yzc1M2YtMzNkMS00ZmQzLTk5YzUtMjlhODk4MTU0Mzg4XC9kOHU3bmZzLWQxZTM3MWQwLTljZGUtNGM5Mi04MjIzLTIzOWVhYjJhMzcyZi5qcGcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.7hRcBCbGzTNqH0dmh4tQ2OYLiTNl-aB9Xl9Ytcmptbs"
                    //3 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d9ers35-b64618cb-23f1-4b13-b493-e5c9bfb4e187.jpg/v1/fill/w_600,h_849,q_75,strp/anna_frozen_steampunk_by_adrianwolve_by_adrianwolve_d9ers35-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD04NDkiLCJwYXRoIjoiXC9mXC8xZjkzNjNjNy1kNWM0LTRjZWUtODY0Yy1jYzI5MGY1NWE0MTNcL2Q5ZXJzMzUtYjY0NjE4Y2ItMjNmMS00YjEzLWI0OTMtZTVjOWJmYjRlMTg3LmpwZyIsIndpZHRoIjoiPD02MDAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.WeOss8HVn6qwUC3U52H6MBm94qBdGF5d9zgFSfvOJWc"
                    4 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d8ki87r-84318d44-0d49-4f37-8763-bd24781953fe.jpg/v1/fill/w_600,h_849,q_75,strp/you_are_in_my_dreams___adrianwolve_by_adrianwolve_d8ki87r-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD04NDkiLCJwYXRoIjoiXC9mXC8xZjkzNjNjNy1kNWM0LTRjZWUtODY0Yy1jYzI5MGY1NWE0MTNcL2Q4a2k4N3ItODQzMThkNDQtMGQ0OS00ZjM3LTg3NjMtYmQyNDc4MTk1M2ZlLmpwZyIsIndpZHRoIjoiPD02MDAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.Sz-6lgou6INuGgmbCwOtkf8msJTUpCMK656JPIioyuI"
                    5 -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d8ntfcy-49317576-47e5-41f8-ada2-613359ab0943.jpg/v1/fill/w_600,h_849,q_75,strp/call_me_dagger___adrian_wolve_by_adrianwolve_d8ntfcy-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD04NDkiLCJwYXRoIjoiXC9mXC8xZjkzNjNjNy1kNWM0LTRjZWUtODY0Yy1jYzI5MGY1NWE0MTNcL2Q4bnRmY3ktNDkzMTc1NzYtNDdlNS00MWY4LWFkYTItNjEzMzU5YWIwOTQzLmpwZyIsIndpZHRoIjoiPD02MDAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.KtvnTD0KMKES3JOdLCKBfsPbkC5JNAez8vsUzOi0MAo"
                    else -> "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/1f9363c7-d5c4-4cee-864c-cc290f55a413/d97urxm-a8bbedfb-baad-4bd3-a209-e307f06dad4f.jpg/v1/fill/w_600,h_849,q_75,strp/i_know_i_buried_it_somewhere____by_adrian_wolve_by_adrianwolve_d97urxm-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3siaGVpZ2h0IjoiPD04NDkiLCJwYXRoIjoiXC9mXC8xZjkzNjNjNy1kNWM0LTRjZWUtODY0Yy1jYzI5MGY1NWE0MTNcL2Q5N3VyeG0tYThiYmVkZmItYmFhZC00YmQzLWEyMDktZTMwN2YwNmRhZDRmLmpwZyIsIndpZHRoIjoiPD02MDAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.PuhtcZSLtDgjuybfZXj65YbJ3Wd4ev_deNc_OP_PhaE"
                }
            }
            val caffsList = mutableListOf<Caff>()

            for (i in 0..5) {
                val image = loadImage(urls[i])
                caffsList.add(
                    Caff(
                        i.toLong(), LocalDateTime.now(), "Adrian Wolve",
                        Random.nextInt(100, 500), image
                    )
                )
            }

            caffs.postValue(caffsList)
        }
    }

    private suspend fun loadImage(urlText: String): Bitmap {
        return withContext(Dispatchers.IO) {
            val url = URL(urlText)
            return@withContext BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
    }

    fun addComment(text: String) {

    }

    fun select(caff: Caff) {
        viewModelScope.launch {
            selectedCaff.postValue(caff)
            comments.postValue(listOf(Comment("Géza", LocalDateTime.now(), "Menő")))
        }
    }

    fun uploadCaff(caff: String) {

    }

    fun downloadCaff(): String {
        return ""
    }

    fun filter(text: String) {
        // TODO: ezt lehet a szereveren kéne és akkor itt szimplán le kell tölteni a szűrt elemeket
    }
}
