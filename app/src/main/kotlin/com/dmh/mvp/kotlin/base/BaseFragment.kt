package com.dmh.mvp.kotlin.base

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.ButterKnife
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.di.component.DaggerMainComponent
import com.dmh.mvp.kotlin.di.component.MainComponent

/**
 * 所有的fragment的基类
 */
abstract class BaseFragment : Fragment(), BaseContract.View {
    /**
     * 加载数据的对话框显示次数的计数
     */
    @Volatile private var loadDialogCount = 0
    /**
     * 加载数据显示的对话框
     */
    private val loadDialog: Dialog by lazy {
        Dialog(activity, R.style.LoadDialog).apply {
            val loadDialogContentView = LayoutInflater.from(activity).inflate(R.layout.layout_load_dialog, null)
            val progressBar = loadDialogContentView.findViewById<View>(R.id.progress_load_dialog) as ProgressBar
            val progressColor = ContextCompat.getColor(activity, R.color.colorLoadDialogProgress)
            progressBar.indeterminateDrawable.setColorFilter(progressColor, PorterDuff.Mode.SRC_ATOP)
            setCanceledOnTouchOutside(BuildConfig.DEBUG)
            setCancelable(true)
            setContentView(loadDialogContentView)
            loadDialogCount = 0
        }
    }

    private var contentView: View? = null
    /**
     * 与本View关联的Presenter
     */
    private lateinit var basePresenter: BaseContract.Presenter<BaseContract.View>

    protected lateinit var activity: AppCompatActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity() as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (contentView == null) {
            contentView = inflater!!.inflate(getLayoutResId(), container, false)
            ButterKnife.bind(this, contentView!!)
            basePresenter = injectPresenter(DaggerMainComponent.builder().build())
            basePresenter.attachView(this)
            start()
            basePresenter.start()
        }
        return contentView
    }

    @LayoutRes
    protected abstract fun getLayoutResId(): Int

    protected abstract fun injectPresenter(component: MainComponent): BaseContract.Presenter<BaseContract.View>

    open protected fun start() {}

    override fun showLoadDialog() {
        if (!loadDialog.isShowing) {
            loadDialog.show()
        }
        loadDialogCount++
    }

    override fun cancelLoadDialog() {
        cancelLoadDialog(false)
    }

    private fun cancelLoadDialog(force: Boolean) {
        if (force) {
            loadDialogCount = 0
        }
        loadDialogCount--
        if (loadDialogCount <= 0) {
            loadDialog.cancel()
        }
    }

    protected fun showToast(msg: String) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(msgId: Int) {
        Toast.makeText(getActivity(), msgId, Toast.LENGTH_SHORT).show()
    }

    protected fun showSnack(msg: String, actionResId: Int, action: Runnable) {
        Snackbar.make(contentView!!, msg, Snackbar.LENGTH_SHORT).setAction(actionResId) { action.run() }.show()
    }

    override fun close() {

    }

    override fun onResume() {
        super.onResume()
        basePresenter.resume()
    }

    override fun onPause() {
        super.onPause()
        basePresenter.pause()
    }

    override fun onDestroy() {
        cancelLoadDialog(true)
        basePresenter.destroy()
        super.onDestroy()
    }
}




