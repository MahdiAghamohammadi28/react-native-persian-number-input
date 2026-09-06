package expo.modules.persiannumberinput

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.facebook.react.common.assets.ReactFontManager
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView

class PersianNumberInputView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
  private val editText = EditText(context)
  val onChangeText by EventDispatcher()
  val onInputFocus by EventDispatcher()
  val onInputBlur by EventDispatcher()
  val onSubmitEditing by EventDispatcher()
  val onEndEditing by EventDispatcher()
  val onKeyPress by EventDispatcher()

  private val digitMap = mapOf(
    // Latin -> Persian
    '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
    '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹',
    // Arabic-Indic -> Persian
    '٠' to '۰', '١' to '۱', '٢' to '۲', '٣' to '۳', '٤' to '۴',
    '٥' to '۵', '٦' to '۶', '٧' to '۷', '٨' to '۸', '٩' to '۹'
  )

  // Converts digits before they're committed to the buffer — no flicker. Kept as its
  // own property (rather than inline in the `filters =` assignment) so setMaxLength()
  // can combine it with an InputFilter.LengthFilter — EditText.filters is a full
  // replace, not an append, so both filters have to be set together every time either
  // one changes. NOTE: this and every other property below must stay declared BEFORE
  // `init {}` — Kotlin runs property initializers and init blocks in textual order, so
  // anything init{} reads has to already be initialized above it.
  private val digitConversionFilter = InputFilter { source, start, end, _, _, _ ->
    val slice = source.subSequence(start, end).toString()
    val converted = slice.map { digitMap[it] ?: it }.joinToString("")
    if (converted == slice) null else converted
  }

  private var isUpdating = false
  private var horizontalAlign: String = "left"
  private var maxLength: Int? = null
  private var autoFocus: Boolean = false
  private var fontFamilyName: String? = null
  private var fontStyle: Int = Typeface.NORMAL
  private var allowFontScaling: Boolean = true
  private var currentFontSizeSp: Float = 17f

  init {
    addView(editText)

    // Force single-line behavior: without this, EditText wraps to a new line once the
    // text is wider than the box, and the wrapped second line gets clipped by the
    // wrapper's overflow:hidden.
    editText.isSingleLine = true
    editText.maxLines = 1
    editText.setHorizontallyScrolling(true)

    // Remove Android's default Material underline/background — the consuming app
    // usually draws its own bordered container around this input in JS.
    editText.background = null
    editText.setPadding(0, 0, 0, 0)
    applyGravity()
    applyFilters()

    editText.setOnFocusChangeListener { _, hasFocus ->
      if (hasFocus) {
        onInputFocus(mapOf())
      } else {
        onInputBlur(mapOf())
        onEndEditing(mapOf("text" to editText.text.toString()))
      }
    }

    editText.setOnEditorActionListener { _, _, _ ->
      onSubmitEditing(mapOf())
      onEndEditing(mapOf("text" to editText.text.toString()))
      false // don't consume — let the IME's default behavior (closing, moving focus) proceed
    }

    // Hardware-key backspace/enter. Soft-keyboard backspace on an already-empty
    // field doesn't reliably reach onKeyListener on all IMEs — flagged as a
    // known gap to revisit (custom InputConnection wrapper) if it matters for
    // your use case.
    editText.setOnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN) {
        val key = when (keyCode) {
          KeyEvent.KEYCODE_DEL -> "Backspace"
          KeyEvent.KEYCODE_ENTER -> "Enter"
          else -> null
        }
        if (key != null) onKeyPress(mapOf("key" to key))
      }
      false
    }

    editText.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
      override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return
        onChangeText(mapOf("text" to (s?.toString() ?: "")))
      }
    })
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    editText.layout(0, 0, r - l, b - t)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (autoFocus) {
      editText.requestFocus()
      val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
      imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
  }

  // MARK: - Props

  // value comes in as LATIN digits (from RHF) — convert to Farsi for display
  fun setValue(value: String) {
    val converted = value.map { digitMap[it] ?: it }.joinToString("")
    if (editText.text.toString() != converted) {
      isUpdating = true
      editText.setText(converted)
      editText.setSelection(converted.length)
      isUpdating = false
    }
  }

  fun setPlaceholder(value: String) {
    editText.hint = value
  }

  private fun applyTextSize() {
    val size = if (allowFontScaling) {
      currentFontSizeSp
    } else {
      // Ignore the user's system font-scale setting by resolving to raw pixels
      // ourselves instead of letting COMPLEX_UNIT_SP apply fontScale automatically.
      currentFontSizeSp * resources.displayMetrics.density
    }
    val unit = if (allowFontScaling) TypedValue.COMPLEX_UNIT_SP else TypedValue.COMPLEX_UNIT_PX
    editText.setTextSize(unit, size)
  }

  fun setFontSize(value: Float) {
    currentFontSizeSp = value
    applyTextSize()
  }

  fun setAllowFontScaling(value: Boolean) {
    allowFontScaling = value
    applyTextSize()
  }

  // Custom fonts (e.g. IranYekan-Regular) are registered by expo-font with RN's own
  // ReactFontManager under the family name used in JS — reuse that registry so this
  // native EditText resolves the exact same typeface as every other <Text>/<TextInput>.
  private fun applyTypeface() {
    val family = fontFamilyName
    editText.typeface = if (family != null) {
      ReactFontManager.getInstance().getTypeface(family, fontStyle, context.assets)
    } else {
      Typeface.create(Typeface.DEFAULT, fontStyle)
    }
  }

  fun setFontFamily(value: String) {
    fontFamilyName = value
    applyTypeface()
  }

  fun setFontWeight(value: String) {
    fontStyle = if (value == "bold") Typeface.BOLD else Typeface.NORMAL
    applyTypeface()
  }

  // Renamed from "color" — "color" is a reserved prop name that React Native's
  // common-props layer expects to be a numeric packed color, which crashes when
  // Expo's Prop declares it as a String. "textColor" avoids the collision.
  fun setTextColor(value: String) {
    try {
      editText.setTextColor(Color.parseColor(value))
    } catch (e: IllegalArgumentException) {
      // Unrecognized color string — ignore instead of crashing, keep previous color.
    }
  }

  fun setPlaceholderTextColor(value: String) {
    try {
      editText.setHintTextColor(Color.parseColor(value))
    } catch (e: IllegalArgumentException) {
      // Unrecognized color string — ignore instead of crashing.
    }
  }

  fun setSelectionColor(value: String) {
    try {
      editText.highlightColor = Color.parseColor(value)
    } catch (e: IllegalArgumentException) {
      // Unrecognized color string — ignore instead of crashing.
    }
  }

  // Tints the blinking cursor itself (distinct from the selection highlight).
  // Requires API 29+; below that Android has no public API for this without
  // risky reflection, so we deliberately no-op on older devices.
  fun setCursorColor(value: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      try {
        val color = Color.parseColor(value)
        val drawable = editText.textCursorDrawable?.mutate()
        drawable?.setTint(color)
        editText.textCursorDrawable = drawable
      } catch (e: IllegalArgumentException) {
        // Unrecognized color string — ignore.
      }
    }
  }

  fun setCaretHidden(value: Boolean) {
    editText.isCursorVisible = !value
  }

  private fun applyGravity() {
    val horizontal = when (horizontalAlign) {
      "right" -> Gravity.END
      "center" -> Gravity.CENTER_HORIZONTAL
      else -> Gravity.START
    }
    editText.gravity = Gravity.CENTER_VERTICAL or horizontal
  }

  fun setTextAlign(value: String) {
    horizontalAlign = value
    applyGravity()
  }

  fun setRTL(enabled: Boolean) {
    editText.layoutDirection = if (enabled) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    editText.textDirection = if (enabled) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
  }

  fun setContentPadding(value: Float) {
    val px = (value * resources.displayMetrics.density).toInt()
    editText.setPadding(px, editText.paddingTop, px, editText.paddingBottom)
  }

  fun setContentPaddingVertical(value: Float) {
    val px = (value * resources.displayMetrics.density).toInt()
    editText.setPadding(editText.paddingLeft, px, editText.paddingRight, px)
  }

  fun setLetterSpacing(value: Float) {
    if (editText.textSize > 0) {
      editText.letterSpacing = value / editText.textSize
    }
  }

  fun setEditable(value: Boolean) {
    editText.isEnabled = value
    editText.isFocusable = value
    editText.isFocusableInTouchMode = value
  }

  fun setSecureTextEntry(value: Boolean) {
    editText.inputType = if (value) {
      InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    } else {
      InputType.TYPE_CLASS_NUMBER
    }
    applyFilters()
  }

  // "sms-otp" turns on the platform SMS Retriever autofill hint so Android can
  // offer to fill in a verification code automatically. Anything else clears it.
  fun setAutoComplete(value: String?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (value == "sms-otp") {
        editText.setAutofillHints(View.AUTOFILL_HINT_SMS_OTP)
        editText.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
      } else {
        editText.setAutofillHints(null as Array<String>?)
      }
    }
  }

  private fun applyFilters() {
    val filters = mutableListOf<InputFilter>(digitConversionFilter)
    maxLength?.let { filters.add(InputFilter.LengthFilter(it)) }
    editText.filters = filters.toTypedArray()
  }

  fun setMaxLength(value: Int?) {
    maxLength = value
    applyFilters()
  }

  fun setAutoFocus(value: Boolean) {
    autoFocus = value
    if (value && isAttachedToWindow) {
      editText.requestFocus()
    }
  }

  fun setSelectTextOnFocus(value: Boolean) {
    editText.setSelectAllOnFocus(value)
  }

  fun setKeyboardType(value: String) {
    editText.inputType = when (value) {
      "numeric" -> InputType.TYPE_CLASS_NUMBER
      "phone-pad" -> InputType.TYPE_CLASS_PHONE
      "email-address" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
      else -> InputType.TYPE_CLASS_TEXT
    }
    // TYPE_CLASS_NUMBER auto-attaches a DigitsKeyListener which only accepts ASCII
    // digits by default — widen it to also accept Persian/Arabic-Indic glyphs so
    // raw input reaches digitConversionFilter, which then normalizes everything.
    if (value == "numeric") {
      editText.keyListener = DigitsKeyListener.getInstance("0123456789٠١٢٣٤٥٦٧٨٩۰۱۲۳۴۵۶۷۸۹")
    }
  }

  fun setReturnKeyType(value: String) {
    editText.imeOptions = when (value) {
      "done" -> EditorInfo.IME_ACTION_DONE
      "next" -> EditorInfo.IME_ACTION_NEXT
      "search" -> EditorInfo.IME_ACTION_SEARCH
      "send" -> EditorInfo.IME_ACTION_SEND
      else -> EditorInfo.IME_ACTION_UNSPECIFIED
    }
  }

  // MARK: - Imperative methods (called from JS via a ref — see PersianNumberInputModule)

  fun focusInput() {
    editText.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
  }

  fun blurInput() {
    editText.clearFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(editText.windowToken, 0)
  }

  fun clearInput() {
    isUpdating = true
    editText.setText("")
    isUpdating = false
    onChangeText(mapOf("text" to ""))
  }

  fun isInputFocused(): Boolean = editText.isFocused
}