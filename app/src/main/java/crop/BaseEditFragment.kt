package crop

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.ImageEditorActivity

abstract class BaseEditFragment : Fragment() {
    protected var activity: ImageEditorActivity? = null

    fun ensureEditActivity(): ImageEditorActivity? {
        if (activity == null) {
            activity = getActivity() as ImageEditorActivity?
        }
        return activity
    }

    /**
     *
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ensureEditActivity()
    }

    /**
     *
     */
    override fun onResume() {
        super.onResume()
        ensureEditActivity()
    }

    /**
     *
     */
    abstract fun onShow()

    /**
     *
     */
    abstract fun backToMain()
}