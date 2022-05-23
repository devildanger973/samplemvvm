package add.text

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import paint.ColorPickerAdapter

class TextEditorDialogFragment : DialogFragment() {
    private var addTextEditText: EditText? = null
    private var inputMethodManager: InputMethodManager? = null
    private var colorCode = 0
    private var onTextEditorListener: SetOnTextEditorListener? = null
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val window = dialog.window
            if (window != null) {
                dialog.window!!.setLayout(width, height)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_text_sticker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addTextEditText = view.findViewById(R.id.add_text_edit_text)
        inputMethodManager =
            (activity ?: return).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val addTextDoneTv = view.findViewById<TextView>(R.id.add_text_done_tv)

        //Setup the color picker for text color
        val addTextColorPickerRecyclerView: RecyclerView =
            view.findViewById(R.id.add_text_color_picker_recycler_view)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireActivity(), object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                this@TextEditorDialogFragment.colorCode = colorCode
                addTextEditText!!.setTextColor(colorCode)
            }
        })

        addTextColorPickerRecyclerView.adapter = colorPickerAdapter
        addTextEditText!!.setText((arguments ?: return).getString(EXTRA_INPUT_TEXT))
        colorCode = (arguments ?: return).getInt(EXTRA_COLOR_CODE)
        addTextEditText!!.setTextColor(colorCode)
        inputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        //Make a callback on activity when user is done with text editing
        addTextDoneTv.setOnClickListener { view1: View ->
            inputMethodManager!!.hideSoftInputFromWindow(view1.windowToken, 0)
            val inputText = addTextEditText!!.getText().toString()
            if (!TextUtils.isEmpty(inputText) && onTextEditorListener != null) {
                onTextEditorListener!!.setOnTextEditorListener(inputText, colorCode)
                //listener.setOnTextEditorListener(inputText, colorCode)
            }
            dismiss()
        }
    }

    interface SetOnTextEditorListener {
        fun setOnTextEditorListener(inputText: String?, colorCode: Int)
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(onTextEditorListener: SetOnTextEditorListener?) {
        this.onTextEditorListener = onTextEditorListener
    }

    companion object {
        val TAG = TextEditorDialogFragment::class.java.simpleName
        private const val EXTRA_INPUT_TEXT = "extra_input_text"
        private const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String = "",
            @ColorInt initialColorCode: Int = ContextCompat.getColor(appCompatActivity,
                R.color.white),
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, initialColorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
    }
}
