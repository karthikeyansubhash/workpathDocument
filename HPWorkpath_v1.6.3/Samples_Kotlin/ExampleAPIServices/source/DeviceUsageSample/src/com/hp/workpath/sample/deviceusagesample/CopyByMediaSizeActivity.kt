package com.hp.workpath.sample.deviceusagesample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hp.workpath.sample.deviceusagesample.fragment.CopyByMediaSizeFragment
import com.hp.workpath.sample.deviceusagesample.fragment.PrintByMediaSizeFragment

class CopyByMediaSizeActivity : AppCompatActivity() {

    private lateinit var mCopyByMediaSizeFragment: CopyByMediaSizeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_copy_by_media_size)

        val bundle = Bundle()
        bundle.putString(DATA, intent.getStringExtra(DATA))
        mCopyByMediaSizeFragment = CopyByMediaSizeFragment()
        mCopyByMediaSizeFragment.arguments = bundle
    }

    override fun onResume() {
        super.onResume()
        replaceFragment(mCopyByMediaSizeFragment)
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