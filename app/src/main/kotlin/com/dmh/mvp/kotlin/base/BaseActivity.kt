package com.dmh.mvp.kotlin.base

import android.app.Dialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.ButterKnife
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.di.component.DaggerMainComponent
import com.dmh.mvp.kotlin.di.component.MainComponent
import com.dmh.mvp.kotlin.utils.ActivityStack

/**
 * Created by QiuGang on 2017/9/27 22:31
 * Email : 1607868475@qq.com
 */

/**
 * 所有的activity的基类
 */
abstract class BaseActivity : AppCompatActivity(), BaseContract.View {
    /**
     * 加载数据的对话框显示次数的计数
     */
    @Volatile private var loadDialogCount = 0
    /**
     * 加载数据显示的对话框
     */
    private val loadDialog: Dialog by lazy {
        Dialog(this, R.style.LoadDialog).apply {
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

    private lateinit var basePresenter: BaseContract.Presenter<BaseContract.View>

    protected lateinit var activity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        setSupportToolbar()
        activity = this
        ActivityStack.get().add(this)
        ButterKnife.bind(this)
        basePresenter = injectPresenter(DaggerMainComponent.builder().build())
        basePresenter.attachView(this)
        start()
        basePresenter.start()
    }

    @LayoutRes
    protected abstract fun getLayoutResId(): Int

    @NonNull
    protected abstract fun injectPresenter(component: MainComponent): BaseContract.Presenter<BaseContract.View>

    open protected fun start() {}

    private fun setSupportToolbar() {
        val toolbar: View? = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportToolbar(toolbar as Toolbar)
        }
    }

    protected fun setSupportToolbar(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_action_back)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val textToolbarTitle: TextView? = toolbar.findViewById<View>(R.id.text_title_in_toolbar) as TextView
        val title = actionBar?.title
        textToolbarTitle?.text = title
        actionBar?.title = ""
        toolbar.setNavigationOnClickListener { finish() }
    }

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
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(msgId: Int) {
        Toast.makeText(activity, msgId, Toast.LENGTH_SHORT).show()
    }

    override fun close() {
        finish()
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
        basePresenter.destroy()
        cancelLoadDialog(true)
        ActivityStack.get().remove(this)
        super.onDestroy()
    }
}