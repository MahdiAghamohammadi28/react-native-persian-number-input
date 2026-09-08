<div align="center">

# 🔢 react-native-persian-number-input

**A native, high-performance numeric input for React Native & Expo — with built-in Persian digit conversion.**

[![npm version](https://img.shields.io/npm/v/react-native-persian-number-input.svg?style=flat-square&color=CB3837&logo=npm)](https://www.npmjs.com/package/react-native-persian-number-input)
[![license](https://img.shields.io/badge/license-MIT-3DA639.svg?style=flat-square)](./LICENSE)
[![platforms](https://img.shields.io/badge/platforms-Android%20%7C%20iOS%20%7C%20Web-1e90ff.svg?style=flat-square)](#platform-support)
[![Expo Modules API](https://img.shields.io/badge/built%20with-Expo%20Modules%20API-000020.svg?style=flat-square&logo=expo)](https://docs.expo.dev/modules/overview/)

<br />

*Latin, Persian, or Arabic-Indic digits go in — clean Persian (`۰۱۲۳۴۵۶۷۸۹`) glyphs come out, live as the user types.*

</div>

---

## ✨ Features

- 🔢 **Automatic digit conversion** — accepts Latin (`0-9`), Persian (`۰-۹`), and Arabic-Indic (`٠-٩`) input and normalizes everything to Persian glyphs as the user types
- ⚡ **Fully native** — a real `EditText` / `UITextField` under the hood on Android and iOS, not a styled wrapper around the RN `<TextInput>`
- 🎛 **`TextInput`-familiar API** — same prop names and a matching `ref` handle (`focus`, `blur`, `clear`, `isFocused`), so it drops in with minimal changes
- 📱 **One-time-code (OTP) autofill** — `autoComplete="sms-otp"` wires up the real platform mechanism (Android SMS Retriever hint, iOS `.oneTimeCode`)
- 🔒 **`secureTextEntry`** for numeric PIN entry
- 🔁 **RTL-aware** out of the box
- 🖋 **Custom font support** — resolves fonts registered via `expo-font`, so it matches the rest of your `<Text>`/`<TextInput>` typography
- 🌐 **Real web support** — not a "not implemented" stub

---

## 📦 Installation

```sh
npx expo install react-native-persian-number-input
```

<details>
<summary>Using bare React Native (no Expo)?</summary>

<br />

This package is built on the **Expo Modules API**, which works in bare RN projects too — you just need the `expo` package installed as a dependency (it does not require the Expo "managed workflow" or EAS).

```sh
npx install-expo-modules@latest
npm install react-native-persian-number-input
npx pod-install
```

</details>

---

## 🚀 Quick start

```tsx
import React, { useRef } from "react";
import { View, Button } from "react-native";
import PersianNumberInput, {
  PersianNumberInputHandle,
} from "react-native-persian-number-input";

export default function Example() {
  const inputRef = useRef<PersianNumberInputHandle>(null);
  const [value, setValue] = React.useState("");

  return (
    <View style={{ padding: 24 }}>
      <PersianNumberInput
        ref={inputRef}
        value={value}
        onChangeText={setValue}
        placeholder="شماره موبایل"
        rtl
        keyboardType="numeric"
        maxLength={11}
        style={{
          fontSize: 18,
          color: "#111",
          borderWidth: 1,
          borderColor: "#ddd",
          borderRadius: 8,
          padding: 12,
        }}
      />

      <Button title="Focus" onPress={() => inputRef.current?.focus()} />
      <Button title="Clear" onPress={() => inputRef.current?.clear()} />
    </View>
  );
}
```

`value`/`onChangeText` always carry **Latin digits** (`"0912..."`) — perfect for sending straight to an API or a form library like `react-hook-form`. The Persian glyphs are a *display-only* conversion that happens natively.

---

## 📖 Props

### Content & behavior

| Prop | Type | Default | Description |
|---|---|---|---|
| `value` | `string` | — | Latin-digit string. Displayed to the user as Persian digits. |
| `onChangeText` | `(text: string) => void` | — | Fires with the Latin-digit value on every change. |
| `placeholder` | `string` | — | Placeholder text. |
| `rtl` | `boolean` | `false` | Right-to-left layout direction. |
| `editable` | `boolean` | `true` | Disables input when `false`. |
| `maxLength` | `number` | — | Maximum digit count. |
| `autoFocus` | `boolean` | `false` | Focuses on mount. |
| `selectTextOnFocus` | `boolean` | `false` | Selects existing text when focused. |
| `secureTextEntry` | `boolean` | `false` | Masks input — for numeric PINs. |
| `autoComplete` | `"off"` \| `"sms-otp"` | `"off"` | `"sms-otp"` enables native one-time-code autofill. |
| `keyboardType` | `"default"` \| `"numeric"` \| `"phone-pad"` \| `"email-address"` | `"default"` | Same values as RN's `TextInput`. |
| `returnKeyType` | `"default"` \| `"done"` \| `"next"` \| `"search"` \| `"send"` | `"default"` | Keyboard return key label. |
| `allowFontScaling` | `boolean` | `true` | Respect the system font-size accessibility setting. |
| `placeholderTextColor` | `string` | — | Placeholder color. |
| `selectionColor` | `string` | — | Text-selection highlight color. |
| `cursorColor` | `string` | — | Blinking-cursor color *(Android 10+ / iOS)*. |
| `caretHidden` | `boolean` | `false` | Hides the cursor entirely. |

### Style

`style` accepts the same layout props as any RN `View` (`width`, `margin`, `borderWidth`, `flex`, ...) **plus** a subset of text props: `color`, `fontSize`, `fontFamily`, `fontWeight` (`"normal"` \| `"bold"`), `letterSpacing`, `textAlign` (`"left"` \| `"right"` \| `"center"`), and `padding`/`paddingHorizontal`/`paddingVertical` — exactly like styling a `<TextInput>`.

### Events

| Prop | Signature | Fires when |
|---|---|---|
| `onChangeText` | `(text: string) => void` | Text changes. |
| `onFocus` | `() => void` | Input gains focus. |
| `onBlur` | `() => void` | Input loses focus. |
| `onSubmitEditing` | `() => void` | The keyboard's return key is pressed. |
| `onEndEditing` | `(text: string) => void` | Editing ends (blur or submit), with the final text. |
| `onKeyPress` | `(key: "Backspace" \| "Enter") => void` | Backspace or Enter is pressed. |

### Ref methods

Attach a `ref` to get an imperative handle, just like RN's own `TextInput`:

| Method | Description |
|---|---|
| `focus()` | Focuses the input and opens the keyboard. |
| `blur()` | Removes focus and closes the keyboard. |
| `clear()` | Clears the current value. |
| `isFocused()` | `Promise<boolean>` — whether the input is currently focused. |

```tsx
const ref = useRef<PersianNumberInputHandle>(null);

await ref.current?.isFocused(); // → boolean
ref.current?.focus();
```

---

## 📲 One-time-code (OTP) autofill

```tsx
<PersianNumberInput
  autoComplete="sms-otp"
  keyboardType="numeric"
  maxLength={5}
  value={code}
  onChangeText={setCode}
/>
```

- **Android** — sets the `AUTOFILL_HINT_SMS_OTP` autofill hint, so the code can be filled automatically when combined with the [SMS Retriever API](https://developers.google.com/identity/sms-retriever/overview).
- **iOS** — sets `textContentType(.oneTimeCode)`, so iOS surfaces the code from Messages above the keyboard automatically.

---

## 🖥 Platform support

| Platform | Implementation |
|---|---|
| Android | Native `EditText` (Kotlin) |
| iOS | Native `UITextField` (Swift) |
| Web | Plain `<input>` with matching digit-conversion logic |

> `allowFontScaling`, `selectionColor`, and `cursorColor` have no standard cross-browser equivalent and are silently ignored on web.

---

## 🤝 Contributing

Issues and PRs are welcome — see [CONTRIBUTING.md](./CONTRIBUTING.md). The `example/` app in this repo is the fastest way to try local changes:

```sh
cd example
npx expo run:android   # or run:ios
```

---

## 📄 License

[MIT](./LICENSE) © [Mahdi Aghamohammadi](https://github.com/MahdiAghamohammadi28)
