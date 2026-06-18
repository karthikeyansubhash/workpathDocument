package com.hp.workpath.sample.deviceusagesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hp.workpath.sample.deviceusagesample.fragment.PlexByMediaSizeFragment

class PlexByMediaSizeActivity : AppCompatActivity() {

    private lateinit var mPlexByMediaSizeFragment: PlexByMediaSizeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plex_by_media_size)

        val bundle = Bundle()
        bundle.putString(DATA, intent.getStringExtra(DATA))
        mPlexByMediaSizeFragment = PlexByMediaSizeFragment()
        mPlexByMediaSizeFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mPlexByMediaSizeFragment)
    }

    private fun replaceFragment(fragment: Fragment?) {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }

    companion object {
        const val DATA = "data"
    }
}