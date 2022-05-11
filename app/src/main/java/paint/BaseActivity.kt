package paint

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object BaseActivity : AppCompatActivity() {
    fun getLoadingDialog(
        context: Context, titleId: Int,
        canCancel: Boolean,
    ): Dialog {
        return getLoadingDialog(context, context.getString(titleId), canCancel)
    }

    fun getLoadingDialog(
        context: Context?, title: String?,
        canCancel: Boolean,
    ): Dialog {
        val dialog = ProgressDialog(context)
        dialog.setCancelable(canCancel)
        dialog.setMessage(title)
        return dialog
    }
}