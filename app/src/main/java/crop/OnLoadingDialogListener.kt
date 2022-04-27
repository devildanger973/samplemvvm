package crop

import io.reactivex.disposables.Disposable

interface OnLoadingDialogListener {
    fun showLoadingDialog()
    fun dismissLoadingDialog()
}