package com.zhuorui.securties.debug.netreference

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.LinearLayout
import base2app.BaseApplication
import base2app.config.Config
import base2app.config.RunConfig
import base2app.config.RunMode
import base2app.infra.MMKVManager
import base2app.ui.activity.AbsActivity
import base2app.util.StatusBarUtil
import base2app.viewbinding.viewBinding
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.databinding.AppActivityZRNetConfigBinding
import com.zrlib.lib_service.service
import java.io.File
import kotlin.system.exitProcess


class ZRNetConfigActivity : AbsActivity() {

    private val binding by viewBinding(AppActivityZRNetConfigBinding::bind)

    override val layout: Int
        get() = R.layout.app_activity_z_r_net_config

    override val acContentRootViewId: Int
        get() = R.id.root_layout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        switch(BaseApplication.baseApplication.config.runMode)
        binding.ci.text =
            binding.ci.text.toString() + "   " + RunConfig(this, "config_" + RunMode.ci.name).domain_api
        binding.out.text =
            binding.out.text.toString() + "   " + RunConfig(this, "config_" + RunMode.out.name).domain_api
        binding.qa.text =
            binding.qa.text.toString() + "   " + RunConfig(this, "config_" + RunMode.qa.name).domain_api
        binding.dev.text =
            binding.dev.text.toString() + "   " + RunConfig(this, "config_" + RunMode.dev.name).domain_api
        binding.preview.text = binding.preview.text.toString() + "   " + RunConfig(
            this,
            "config_" + RunMode.preview.name
        ).domain_api

        binding.group.setOnCheckedChangeListener { _, checkedId ->
            var config = "binding.qa"
            when (checkedId) {
                R.id.ci -> config = "ci"
                R.id.out -> config = "out"
                R.id.qa -> config = "qa"
                R.id.dev -> config = "dev"
                R.id.preview -> config = "preview"
            }
            clearAllCache(applicationContext)
            //保存当前环境
            saveNetConfig(config)
            //启动app
            restartApplication()
        }

        val barHeight = StatusBarUtil.getStatusBarHeight(this)
        (binding.netTitleBar.layoutParams as LinearLayout.LayoutParams).topMargin = barHeight
        binding.netBack.setOnClickListener { finish() }
    }

    private fun saveNetConfig(config: String) {
        MMKVManager.getInstance().close()
        MMKVManager.getInstance().init(this)
        MMKVManager.getInstance().putString(Config.CACHE_CONFIG_NAME, config)
    }

    private fun restartApplication() {
        binding.group.postDelayed({
            killPushProcess()
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }, 500)
    }

    /**
     * 切换环境，杀死推送进程，避免切换环境时新的appId和appKey初始化无效
     */
    private fun killPushProcess() {
        BaseApplication.context?.let { safeContext ->
            val activityManager = safeContext.getSystemService(Context.ACTIVITY_SERVICE)
            if (activityManager is ActivityManager) {
                activityManager.runningAppProcesses?.forEach {
                    if (it.processName == "${safeContext.packageName}:pushservice") {
                        android.os.Process.killProcess(it.pid)
                        return
                    }
                }
            }
        }
    }


    //删除内外缓存
    private fun clearAllCache(context: Context) {
        deleteDir(context.cacheDir)
        deleteDir(File("/data/data/$packageName"))
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteDir(context.externalCacheDir)
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    private fun switch(runMode: RunMode?) {
        when (runMode) {
            RunMode.ci -> binding.group.check(R.id.ci)
            RunMode.out -> binding.group.check(R.id.out)
            RunMode.qa -> binding.group.check(R.id.qa)
            RunMode.dev -> binding.group.check(R.id.dev)
            RunMode.preview -> binding.group.check(R.id.preview)
        }
    }
}