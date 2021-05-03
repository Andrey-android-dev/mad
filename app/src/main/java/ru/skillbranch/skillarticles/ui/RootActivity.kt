package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.delegates.ViewBindingDelegate
import ru.skillbranch.skillarticles.ui.delegates.viewBinding
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity(), IArticleView {

    private val viewModel: ArticleViewModel by viewModels { ViewModelFactory("0") }

    //private lateinit var vb: ActivityRootBinding
    private val vb: ActivityRootBinding by ViewBindingDelegate(this, ActivityRootBinding::inflate)
    //private val vb: ActivityRootBinding by viewBinding(ActivityRootBinding::inflate)

    private val vbBottombar
        get() = vb.bottombar.binding

    private val vbSubmenu
        get() = vb.submenu.binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        setupToolbar()
        setupBottombar()
        setupSubmenu()

        viewModel.observeState(this) {
            renderUi(it)
        }
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.article_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = (searchItem?.actionView as SearchView)

        if (viewModel.state.value?.isSearch == true) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.state.value?.searchQuery, false)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.handleSearchMode(false)
                return true
            }
        })
        return true
    }

    override fun setupSubmenu() {
        vbSubmenu.btnTextUp.setOnClickListener { viewModel.handleUpText() }
        vbSubmenu.btnTextDown.setOnClickListener { viewModel.handleDownText() }
        vbSubmenu.switchMode.setOnClickListener { viewModel.handleNightMode() }
    }

    override fun setupBottombar() {
        vbBottombar.btnLike.setOnClickListener { viewModel.handleLike() }
        vbBottombar.btnBookmark.setOnClickListener { viewModel.handleBookmark() }
        vbBottombar.btnShare.setOnClickListener { viewModel.handleShare() }
        vbBottombar.btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
    }

    override fun renderUi(data: ArticleState) {
        vbBottombar.btnSettings.isChecked = data.isShowMenu
        if (data.isShowMenu) vb.submenu.open() else vb.submenu.close()

        vbBottombar.btnLike.isChecked = data.isLike
        vbBottombar.btnBookmark.isChecked = data.isBookmark

        vbSubmenu.switchMode.isChecked = data.isDarkMode
        delegate.localNightMode =
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            vb.tvTextContent.textSize = 18f
            vbSubmenu.btnTextUp.isChecked = true
            vbSubmenu.btnTextDown.isChecked = false
        } else {
            vb.tvTextContent.textSize = 14f
            vbSubmenu.btnTextUp.isChecked = false
            vbSubmenu.btnTextDown.isChecked = true
        }

        vb.tvTextContent.text =
            if (data.isLoadingContent) "loading..." else data.content.first() as String

        vb.toolbar.title = data.title ?: "Skill Articles"
        vb.toolbar.subtitle = data.category ?: "loading..."
        if (data.categoryIcon != null) vb.toolbar.logo =
            ContextCompat.getDrawable(this, data.categoryIcon as Int)
    }


    override fun setupToolbar() {
        setSupportActionBar(vb.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val logo = vb.toolbar.children.find { it is AppCompatImageView } as? ImageView
        logo ?: return
        logo.scaleType = ImageView.ScaleType.CENTER_CROP

        (logo.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        TODO("Not yet implemented")
    }

    override fun renderSearchPosition(searchPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun clearSearchResult() {
        TODO("Not yet implemented")
    }

    override fun showSearchBar() {
        TODO("Not yet implemented")
    }

    override fun hideSearchBar() {
        TODO("Not yet implemented")
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(vb.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
            .setAnchorView(vb.bottombar)
            .setActionTextColor(getColor(R.color.color_accent_dark))

        when (notify) {
            is Notify.TextMessage -> {

            }
            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler.invoke()
                }

            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

}