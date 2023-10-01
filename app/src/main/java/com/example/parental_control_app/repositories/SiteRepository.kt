package com.example.parental_control_app.repositories

import android.util.Log
import com.example.parental_control_app.data.Site
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SiteRepository {

    private val db = Firebase.firestore

    suspend fun getSites(uid: String) : List<Site> {
        val completable = CompletableDeferred<List<Site>>()

        coroutineScope {
            launch(Dispatchers.IO) {
                val documents = db.collection("profiles/$uid/sites")
                val sites = documents.get().await()
                completable.complete(sites.toObjects(Site::class.java))
            }
        }

        return completable.await()
    }

    suspend fun addSite(uid: String, site: Site) : String {
        val completableMessage = CompletableDeferred("")

        coroutineScope {
            launch(Dispatchers.IO){
                val document = db.collection("profiles/$uid/sites").document()
                document.set(site)
                    .addOnSuccessListener {
                        completableMessage.complete("Site added to list")
                    }
                    .addOnFailureListener {
                        it.localizedMessage?.let { it1 -> completableMessage.complete(it1) }
                    }
            }
        }

        return completableMessage.await()
    }

    private suspend fun getSiteDocumentId(uid: String, site: Site) : String? {
        val completable = CompletableDeferred<String?>(null)

        coroutineScope {
            launch(Dispatchers.IO) {
                val query = db.collection("profiles/$uid/sites").whereEqualTo("url", site.url).limit(1)
                val result = query.get().await()
                val document = result.documents[0]
                completable.complete(document.id)
            }
        }

        return completable.await()
    }

    suspend fun deleteSite(uid: String, site: Site) : String {
        val completableMessage = CompletableDeferred("")

        coroutineScope {
            launch(Dispatchers.IO){
                val documentId = getSiteDocumentId(uid, site)
                Log.w("DOCUMENT ID", documentId!!)

                db.collection("profiles/$uid/sites").document(documentId).delete()
                    .addOnSuccessListener { completableMessage.complete("Deleted ${site.url} from list") }
                    .addOnFailureListener { it.localizedMessage?.let { it1 ->
                        completableMessage.complete(
                            it1
                        )
                    } }
            }
        }

        return completableMessage.await()
    }

}