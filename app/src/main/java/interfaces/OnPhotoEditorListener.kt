package interfaces

interface OnPhotoEditorListener {
    fun onAddViewListener(numberOfAddedViews: Int)
    fun onRemoveViewListener(numberOfAddedViews: Int)
    fun onStartViewChangeListener()
    fun onStopViewChangeListener()
}
