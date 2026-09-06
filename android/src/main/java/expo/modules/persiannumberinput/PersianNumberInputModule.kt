package expo.modules.persiannumberinput

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class PersianNumberInputModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("PersianNumberInput")

    Events("onChange")

    View(PersianNumberInputView::class) {
    }
  }
}
