package com.udacity

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext



class LoadingData(private val app: Application, private val idForRequest: Long) : LiveData<DownloadData>(),
    CoroutineScope {

    private val downloadManagers by lazy {
        app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    private val query = DownloadManager.Query()

    private val downloadJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + downloadJob


    init {
        query.setFilterById(this.idForRequest)
    }
    override fun onInactive() {
        super.onInactive()
        downloadJob.cancel()
    }

    @InternalCoroutinesApi
    override fun onActive() {
        super.onActive()
        launch {
            while (isActive) {
                val cursor = downloadManagers.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (downloadStatus) {
                        DownloadManager.STATUS_SUCCESSFUL,
                        DownloadManager.STATUS_PENDING,
                        DownloadManager.STATUS_FAILED,
                        DownloadManager.STATUS_PAUSED -> postValue(DownloadData(downloadStatus = downloadStatus))
                        else -> {
                            val loadedInTimeBt = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)).toLong()
                            val sizeOfFileInBt = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)).toLong()
                            postValue(
                                DownloadData(
                                    loadedInTimeBt,
                                    sizeOfFileInBt,
                                    downloadStatus
                                )
                            )
                        }
                    }
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL || downloadStatus == DownloadManager.STATUS_FAILED)
                        cancel()
                } else {
                    postValue(DownloadData(downloadStatus = DownloadManager.STATUS_FAILED))
                    cancel()
                }
                cursor.close()
                delay(500)
            }
        }
    }


}
