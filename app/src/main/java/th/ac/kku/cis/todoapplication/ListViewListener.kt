package th.ac.kku.cis.todoapplication

interface ListViewListener {
    fun onUpdateItem(itemId: String, index: Int, isDone:Boolean)
    fun onDeleteItem(itemId: String, index: Int)
}