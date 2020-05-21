package com.cisdi.qingzhu.ui.base

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cisdi.qingzhu.ui.R
import com.cisdi.qingzhu.ui.util.ActivityStack
import com.cisdi.qingzhu.ui.util.BarUtils
import com.google.android.material.appbar.AppBarLayout

/**
 * 定义所有activity的基类
 *
 * @author lh
 */
abstract class BaseActivity : AppCompatActivity() {
    /**
     * 上下文
     */
    lateinit var mContext: AppCompatActivity

    /**
     * Toolbar
     */
    protected val mToolbar: Toolbar? by lazy { findViewById(R.id.toolbar) }

    /**
     * AppBarLayout
     */
    protected val mAppBarLayout: AppBarLayout? by lazy { findViewById(R.id.app_bar_layout) }

    /**
     * AppBar line
     */
    protected var mAppBarLine: View? = null

    /**
     * 处理ActionBar
     */
    private val mActionBarHelper: ActionBarHelper by lazy { createActionBarHelper() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindowFlags()
        // reverseStatusColor()
        setContentView(getLayout())
        mContext = this
        ActivityStack.addActivity(this)
        initToolbar(savedInstanceState)
        initData()
        initListener()
    }

    /**
     * init window flags
     */
    protected open fun initWindowFlags() {

    }

    /**
     * set layout res id
     */
    protected abstract fun getLayout(): Int

    /**
     * init view & data
     */
    protected abstract fun initData()

    /**
     * set listener
     */
    protected abstract fun initListener()

    protected open fun initToolbar(savedInstanceState: Bundle?) {
        if (this.mToolbar == null || this.mAppBarLayout == null) {
            return
        }
        this.setSupportActionBar(this.mToolbar)
        this.mActionBarHelper.init()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mAppBarLayout?.elevation = 0f
        }
        setStatusBarHeight()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        mActionBarHelper.setTitle(title)
    }

    /**
     * set the AppBarLayout alpha
     *
     * @param alpha alpha
     */
    protected fun setAppBarLayoutAlpha(alpha: Float) {
        if (mAppBarLayout == null) return
        this.mAppBarLayout!!.alpha = alpha
    }

    /**
     * set the AppBarLayout visibility
     *
     * @param visibility visibility
     */
    protected fun setAppBarLayoutVisibility(visibility: Boolean) {
        if (mAppBarLayout == null) return
        this.mAppBarLayout!!.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    protected fun setAppBarLineVisibility(visibility: Boolean) {
        if (mAppBarLine == null) return
        this.mAppBarLine!!.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun setStatusBarHeight() {
        val statusBar: View? = findViewById(R.id.view_status_bar)
        if (statusBar != null && statusBar.visibility == View.VISIBLE) {
            val layoutParams = statusBar.layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = BarUtils.getStatusBarHeight()
            statusBar.layoutParams = layoutParams
        }
    }

    /**
     * Create a compatible helper that will manipulate the action bar if available.
     */
    private fun createActionBarHelper(): ActionBarHelper {
        return ActionBarHelper()
    }

    inner class ActionBarHelper {
        private var mActionBar: ActionBar? = null
        private var mDrawerTitle: CharSequence? = null
        private var mTitle: CharSequence? = null
        private var toolbarTitle: TextView? = null

        init {
            this.mActionBar = supportActionBar
        }

        fun init() {
            if (this.mActionBar == null || mToolbar == null) return
            this.mActionBar!!.setDisplayHomeAsUpEnabled(true)
            this.mActionBar!!.setDisplayShowHomeEnabled(false)
            mDrawerTitle = title
            this.mTitle = mDrawerTitle
            toolbarTitle = mToolbar!!.findViewById(R.id.toolbar_title) as TextView
            if (toolbarTitle != null && supportActionBar != null) {
                supportActionBar!!.setDisplayShowTitleEnabled(false)
                toolbarTitle!!.text = mTitle
            }
        }

        fun setTitle(title: CharSequence) {
            this.mTitle = title
            toolbarTitle?.text = mTitle
        }

        fun setDisplayHomeAsUpEnabled(showHomeAsUp: Boolean) {
            this.mActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            this.onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        ActivityStack.finishActivity(this)
        super.onDestroy()
    }

}