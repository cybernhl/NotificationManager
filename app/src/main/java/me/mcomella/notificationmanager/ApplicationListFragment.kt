package me.mcomella.notificationmanager

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

private val ARG_APPS = "apps"

class ApplicationListFragment() : Fragment() {

    companion object Factory {
        fun newInstance(apps: List<String>): ApplicationListFragment {
            val frag = ApplicationListFragment()

            val args = Bundle()
            args.putStringArray(ARG_APPS, apps.toTypedArray())
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_application_list, container, false)
        initApplicationList(rootView)
        return rootView
    }

    private fun initApplicationList(rootView: View) {
        val apps = arguments.getStringArray(ARG_APPS).toList()

        val applicationList = rootView.findViewById(R.id.applicationList) as RecyclerView
        applicationList.adapter = ApplicationListAdapter(context, apps)
        applicationList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}

private class ApplicationListAdapter(context: Context, val apps: List<String>) :
        RecyclerView.Adapter<ApplicationListAdapter.ViewHolder>() {

    val pkgManager = context.packageManager

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.
                from(parent!!.context).
                inflate(R.layout.application_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val label: CharSequence
        val icon: Drawable?
        val pkgName = apps[position]
        try {
            val appInfo = pkgManager.getApplicationInfo(pkgName, 0)
            label = appInfo.loadLabel(pkgManager)
            icon = appInfo.loadIcon(pkgManager)
        } catch (e: PackageManager.NameNotFoundException) {
            label = pkgName
            icon = null
        }
        holder.title.text = label
        holder.icon.setImageDrawable(icon)

        holder.toggle.isChecked = true
        /*
        holder.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            // Thread-safe: only updated from UI thread.
            app.isChecked = isChecked
            diskManager.saveUserContextsToDisk(installedAppInfo.associateBy({it.pkgName}, {it.isChecked})) // TODO: converting each time is slow.
        }
        */
    }

    private class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val title = itemView!!.findViewById(R.id.title) as TextView
        val icon = itemView!!.findViewById(R.id.icon) as ImageView
        val toggle = itemView!!.findViewById(R.id.toggle) as Switch
    }
}
