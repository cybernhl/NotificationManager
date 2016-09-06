package me.mcomella.notificationmanager

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction

import kotlinx.android.synthetic.main.activity_application_list.*

class ContextSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_list)
        attachClickListeners()
    }

    private fun attachClickListeners() {
        /* TODO: fix permissions again.
        notification_settings_button.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        */
        fab.setOnClickListener {
            showAddToListDialog()
        }
    }

    // via https://developer.android.com/reference/android/app/DialogFragment.html
    private val DIALOG_TAG = "dialog"
    private fun showAddToListDialog() {
        val ft = supportFragmentManager!!.beginTransaction()
        val prev = supportFragmentManager!!.findFragmentByTag(DIALOG_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        val newFrag = AddToListDialogFragment.newInstance()
        newFrag.show(ft, DIALOG_TAG)
    }

    override fun onStart() {
        super.onStart()

        tabsLayout.tabMode = TabLayout.MODE_SCROLLABLE
        contentPager.adapter = UserContextAdapter(this, supportFragmentManager)
        tabsLayout.setupWithViewPager(contentPager)

        startNotificationService()
    }

    private fun startNotificationService() {
        val intent = Intent(this, NotificationService::class.java)
        startService(intent) // TODO: start service on device startup too.
    }
}

private class UserContextAdapter(context: Context, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    val diskManager = DiskManager(context)
    val userContexts = diskManager.readUserContextsFromDisk()

    override fun getCount(): Int {
        return userContexts.size
    }

    override fun getItem(position: Int): Fragment {
        return ApplicationListFragment.newInstance(userContexts[position].apps)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return userContexts[position].name
    }
}
