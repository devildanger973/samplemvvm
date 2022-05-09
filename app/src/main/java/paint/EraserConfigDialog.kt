package paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EraserConfigDialog : BottomSheetDialogFragment(), OnSeekBarChangeListener {
    private var mProperties: Properties? = null

    interface Properties {
        fun onBrushSizeChanged(brushSize: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_eraser_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val eraserSizeSb = view.findViewById<SeekBar>(R.id.sbSize)
        eraserSizeSb.setOnSeekBarChangeListener(this)
    }

    fun setPropertiesChangeListener(properties: PaintFragment) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbSize) {
            if (mProperties != null) {
                mProperties!!.onBrushSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
