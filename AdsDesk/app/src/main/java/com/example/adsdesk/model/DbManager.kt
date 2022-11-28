package com.example.adsdesk.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {
    val db = Firebase.database.getReference(MAIN_NOD)
    val dbStorage = Firebase.storage.getReference(MAIN_NOD)
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener) {
        if (auth.uid != null) db.child(ad.key ?: "Empty")
            .child(auth.uid!!).child(AD_NOD).setValue(ad)
            .addOnCompleteListener {
                val adFilter = AdFilter(ad.time, "${ad.category}_${ad.time}")
                db.child(ad.key ?: "Empty").child(FILTER_NOD).setValue(adFilter)
                    .addOnCompleteListener {
                        finishListener.onFinish()
                    }
            }
    }
    fun adViewed(ad: Ad){
        var counter = ad.viewsCounter.toInt()
        counter++
        if(auth.uid != null) db.child(ad.key ?: "Empty")
            .child(INFO_NOD).setValue(InfoItem(counter.toString(),ad.emailCounter, ad.callsCounter))
    }

    fun onFavsClick(ad: Ad, listener: FinishWorkListener){
        if (ad.isFav){
            removeFromFavs(ad, listener)
        } else {
            adToFavs(ad, listener)
        }
    }

    private fun adToFavs(ad: Ad, listener: FinishWorkListener){
        ad.key?.let {
            auth.uid?.let {
                uid -> db.child(it).child(FAVS_NOD).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful){
                        listener.onFinish()
                    }
                }
            }
        }
    }

    private fun removeFromFavs(ad: Ad, listener: FinishWorkListener){
        ad.key?.let {
            auth.uid?.let {
                    uid -> db.child(it).child(FAVS_NOD).child(uid).removeValue().addOnCompleteListener {
                if (it.isSuccessful){
                    listener.onFinish()
                }
            }
            }
        }
    }

    fun getMyFavs(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getMyAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFirstPage(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/time").limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }
    fun getAllAdsNextPage(time: String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/time").endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }
    fun getAllAdsFromCatFirstPage(cat: String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/catTime")
            .startAt(cat).endAt(cat + "_\uf8ff").limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }
    fun getAllAdsFromCatNextPage(catTime: String, readDataCallback: ReadDataCallback?){
        val query = db.orderByChild("/adFilter/catTime").endBefore(catTime).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }
// FinishWorkListener нужен для тоого чтобы знать когда в датабазе удалятся данные, то сделать чтото
    fun deleteAd(ad: Ad, listener: FinishWorkListener){
    if (ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) listener.onFinish()
        }
    }

    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<Ad>()
                for(item in snapshot.children){
                    var ad: Ad? = null
                    item.children.forEach {
                        if (ad == null) ad = it.child(AD_NOD).getValue(Ad::class.java)
                    }
                    val infoItem = item.child(INFO_NOD).getValue(InfoItem::class.java)

                    val favCounter = item.child(FAVS_NOD).childrenCount
                    val isFav = auth.uid?.let { item.child(FAVS_NOD).child(it).getValue(String::class.java) }
                    ad?.isFav = isFav != null
                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"

                    if (ad != null) {
                        adArray.add(ad!!)
                    }
                }
                readDataCallback?.readData(adArray)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    interface ReadDataCallback {
        fun readData(list: ArrayList<Ad>)
    }

    interface FinishWorkListener{
        fun onFinish()
    }

    companion object{
        const val AD_NOD = "ad"
        const val FILTER_NOD = "adFilter"
        const val MAIN_NOD = "main"
        const val INFO_NOD = "info"
        const val FAVS_NOD = "favs"
        const val ADS_LIMIT = 2
    }
}