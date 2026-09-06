package expo.modules.persiannumberinput

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class PersianNumberInputModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("PersianNumberInput")

    View(PersianNumberInputView::class) {
      Events(
        "onChangeText",
        "onInputFocus",
        "onInputBlur",
        "onSubmitEditing",
        "onEndEditing",
        "onKeyPress"
      )

      Prop("value") { view: PersianNumberInputView, value: String ->
        view.setValue(value)
      }
      Prop("placeholder") { view: PersianNumberInputView, value: String ->
        view.setPlaceholder(value)
      }
      Prop("fontSize") { view: PersianNumberInputView, value: Float ->
        view.setFontSize(value)
      }
      Prop("allowFontScaling") { view: PersianNumberInputView, value: Boolean ->
        view.setAllowFontScaling(value)
      }
      Prop("fontFamily") { view: PersianNumberInputView, value: String ->
        view.setFontFamily(value)
      }
      Prop("fontWeight") { view: PersianNumberInputView, value: String ->
        view.setFontWeight(value)
      }
      // Renamed from "color" to avoid colliding with RN's reserved common prop.
      Prop("textColor") { view: PersianNumberInputView, value: String ->
        view.setTextColor(value)
      }
      Prop("placeholderTextColor") { view: PersianNumberInputView, value: String ->
        view.setPlaceholderTextColor(value)
      }
      Prop("selectionColor") { view: PersianNumberInputView, value: String ->
        view.setSelectionColor(value)
      }
      Prop("cursorColor") { view: PersianNumberInputView, value: String ->
        view.setCursorColor(value)
      }
      Prop("caretHidden") { view: PersianNumberInputView, value: Boolean ->
        view.setCaretHidden(value)
      }
      Prop("textAlign") { view: PersianNumberInputView, value: String ->
        view.setTextAlign(value)
      }
      Prop("rtl") { view: PersianNumberInputView, value: Boolean ->
        view.setRTL(value)
      }
      Prop("contentPadding") { view: PersianNumberInputView, value: Float ->
        view.setContentPadding(value)
      }
      Prop("contentPaddingVertical") { view: PersianNumberInputView, value: Float ->
        view.setContentPaddingVertical(value)
      }
      Prop("letterSpacing") { view: PersianNumberInputView, value: Float ->
        view.setLetterSpacing(value)
      }
      Prop("editable") { view: PersianNumberInputView, value: Boolean ->
        view.setEditable(value)
      }
      Prop("secureTextEntry") { view: PersianNumberInputView, value: Boolean ->
        view.setSecureTextEntry(value)
      }
      Prop("autoComplete") { view: PersianNumberInputView, value: String? ->
        view.setAutoComplete(value)
      }
      Prop("maxLength") { view: PersianNumberInputView, value: Int? ->
        view.setMaxLength(value)
      }
      Prop("autoFocus") { view: PersianNumberInputView, value: Boolean ->
        view.setAutoFocus(value)
      }
      Prop("selectTextOnFocus") { view: PersianNumberInputView, value: Boolean ->
        view.setSelectTextOnFocus(value)
      }
      Prop("keyboardType") { view: PersianNumberInputView, value: String ->
        view.setKeyboardType(value)
      }
      Prop("returnKeyType") { view: PersianNumberInputView, value: String ->
        view.setReturnKeyType(value)
      }

      // Imperative methods — callable from JS as `nativeRef.current.focus()` etc.
      // once the ref points at the underlying native view. See src/PersianNumberInputView.tsx
      // for the forwardRef/useImperativeHandle wiring that exposes these as
      // ref.current.focus()/blur()/clear()/isFocused() to consumers.
      AsyncFunction("focus") { view: PersianNumberInputView ->
        view.focusInput()
      }
      AsyncFunction("blur") { view: PersianNumberInputView ->
        view.blurInput()
      }
      AsyncFunction("clear") { view: PersianNumberInputView ->
        view.clearInput()
      }
      AsyncFunction("isFocused") { view: PersianNumberInputView ->
        view.isInputFocused()
      }
    }
  }
}