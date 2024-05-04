package github.leavesczy.wififiletransfer.receiver

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import github.leavesczy.wififiletransfer.BaseActivity
import github.leavesczy.wififiletransfer.R
import github.leavesczy.wififiletransfer.common.FileTransferViewState
import kotlinx.coroutines.launch

/**
 * @Author: CZY
 * @Date: 2022/9/26 17:09
 * @Desc:
 */
class FileReceiverActivity : BaseActivity() {

    private val fileReceiverViewModel by viewModels<FileReceiverViewModel>()

    private val tvState by lazy {
        findViewById<TextView>(R.id.tvState)
    }

    private val btnStartReceive by lazy {
        findViewById<Button>(R.id.btnStartReceive)
    }

    private val ivImage by lazy {
        findViewById<ImageView>(R.id.ivImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_receiver)
        supportActionBar?.title = "文件接收端"
        btnStartReceive.setOnClickListener {
            tvState.text = ""
            ivImage.load(data = null)
            fileReceiverViewModel.startListener()
        }
        initEvent()
    }

    private fun initEvent() {
        lifecycleScope.launch {
            launch {
                fileReceiverViewModel.fileTransferViewState.collect {
                    when (it) {
                        FileTransferViewState.Idle -> {
                            tvState.text = ""
                            dismissLoadingDialog()
                        }

                        FileTransferViewState.Connecting -> {
                            showLoadingDialog()
                        }

                        is FileTransferViewState.Receiving -> {
                            showLoadingDialog()
                        }

                        is FileTransferViewState.Success -> {
                            dismissLoadingDialog()
                            ivImage.load(data = it.file)
                        }

                        is FileTransferViewState.Failed -> {
                            dismissLoadingDialog()
                        }
                    }
                }
            }
            launch {
                fileReceiverViewModel.log.collect {
                    tvState.append(it)
                    tvState.append("\n\n")
                }
            }
        }
    }

}