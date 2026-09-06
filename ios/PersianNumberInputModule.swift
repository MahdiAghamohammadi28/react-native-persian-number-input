import ExpoModulesCore

public class PersianNumberInputModule: Module {
  public func definition() -> ModuleDefinition {
    Name("PersianNumberInput")

    Events("onChange")

    View(PersianNumberInputView.self) {
    }
  }
}
