package com.example.myfirstapp.base.app;

import android.content.Context;
import android.util.Log
import androidx.work.CoroutineWorker

import androidx.work.WorkerParameters;
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.ui.Sys.Companion.makeStatusNotification


class PendingWorker<T: Item>(ctx:Context, params:WorkerParameters) : CoroutineWorker(ctx, params) {
    val TAG = "PendingWorker"

    override suspend fun doWork(): Result {
        val appContext = (applicationContext as Containered<T, ItemController<T,*,*,*>>)

        var successful:Int =0
        var failed:Int =0

        for(rezervare in appContext.container.itemDao().getAll()){
            if(!rezervare.isUpdated) {
                Log.d("PendingWorker", "REQUIRES UPDATE ${rezervare}")
                val res = appContext.container.itemRepository.update(rezervare)
                if(!res.isUpdated)
                    failed++
                else successful++
            }
        }

        return try {
            if(successful!=0 || failed!=0)
                applicationContext.makeStatusNotification("Pushed items to database: ${successful} succeeded, ${failed} failed.")
            Result.success()
        } catch (throwable: Throwable) {
            Log.d(TAG, "PendingWorker error : "+throwable.message)
            Result.failure()
        }
    }
}

