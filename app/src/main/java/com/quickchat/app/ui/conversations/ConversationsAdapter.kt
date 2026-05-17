package com.quickchat.app.ui.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.quickchat.app.R
import com.quickchat.app.data.model.Conversation
import com.quickchat.app.util.TimeFormatter

/**
 * RecyclerView adapter for the conversations list.
 */
class ConversationsAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationsAdapter.ViewHolder>(ConversationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarText: TextView = itemView.findViewById(R.id.text_avatar)
        private val contactName: TextView = itemView.findViewById(R.id.text_contact_name)
        private val lastMessage: TextView = itemView.findViewById(R.id.text_last_message)
        private val timestamp: TextView = itemView.findViewById(R.id.text_timestamp)
        private val unreadBadge: TextView = itemView.findViewById(R.id.text_unread_badge)

        fun bind(conversation: Conversation) {
            avatarText.text = conversation.avatarInitial.toString()
            contactName.text = conversation.contactName
            lastMessage.text = conversation.lastMessage
            timestamp.text = TimeFormatter.formatRelative(conversation.lastMessageTimestamp)

            if (conversation.unreadCount > 0) {
                unreadBadge.visibility = View.VISIBLE
                unreadBadge.text = conversation.unreadCount.toString()
                contactName.setTypeface(null, android.graphics.Typeface.BOLD)
                lastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                unreadBadge.visibility = View.GONE
                contactName.setTypeface(null, android.graphics.Typeface.NORMAL)
                lastMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            itemView.setOnClickListener { onConversationClick(conversation) }
        }
    }

    private class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    }
}
//testing after using new pipeline12 test