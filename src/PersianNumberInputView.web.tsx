import React, { forwardRef, useImperativeHandle, useRef, CSSProperties } from 'react';

import { PersianNumberInputHandle, PersianNumberInputProps } from './PersianNumberInput.types';

// Same digit map as the Android/iOS native implementations, kept in sync by
// hand — there are only three of these across the whole package.
const digitMap: Record<string, string> = {
  '0': '۰',
  '1': '۱',
  '2': '۲',
  '3': '۳',
  '4': '۴',
  '5': '۵',
  '6': '۶',
  '7': '۷',
  '8': '۸',
  '9': '۹',
  '٠': '۰',
  '١': '۱',
  '٢': '۲',
  '٣': '۳',
  '٤': '۴',
  '٥': '۵',
  '٦': '۶',
  '٧': '۷',
  '٨': '۸',
  '٩': '۹',
};

function toPersianDigits(value: string): string {
  return value.replace(/[0-9٠-٩]/g, (d) => digitMap[d] ?? d);
}

const PersianNumberInput = forwardRef<PersianNumberInputHandle, PersianNumberInputProps>(
  function PersianNumberInput(
    {
      value,
      placeholder,
      style,
      rtl,
      editable = true,
      maxLength,
      autoFocus,
      selectTextOnFocus,
      allowFontScaling: _allowFontScaling, // no web equivalent — system handles text zoom
      placeholderTextColor,
      selectionColor: _selectionColor, // no standard cross-browser equivalent
      cursorColor: _cursorColor, // no standard cross-browser equivalent
      caretHidden,
      secureTextEntry,
      autoComplete,
      returnKeyType: _returnKeyType, // no web equivalent
      onChangeText,
      onFocus,
      onBlur,
      onSubmitEditing,
      onEndEditing,
      onKeyPress,
    },
    ref
  ) {
    const inputRef = useRef<HTMLInputElement>(null);

    useImperativeHandle(ref, () => ({
      focus: () => inputRef.current?.focus(),
      blur: () => inputRef.current?.blur(),
      clear: () => {
        if (inputRef.current) inputRef.current.value = '';
      },
      isFocused: async () => document.activeElement === inputRef.current,
    }));

    const flatStyle = (Array.isArray(style) ? Object.assign({}, ...style) : style) as
      CSSProperties | undefined;

    return (
      <input
        ref={inputRef}
        type={secureTextEntry ? 'password' : 'tel'}
        inputMode="numeric"
        dir={rtl ? 'rtl' : 'ltr'}
        value={value !== undefined ? toPersianDigits(value) : undefined}
        placeholder={placeholder}
        disabled={!editable}
        maxLength={maxLength}
        autoFocus={autoFocus}
        autoComplete={autoComplete === 'sms-otp' ? 'one-time-code' : 'off'}
        style={{
          border: 'none',
          outline: 'none',
          background: 'transparent',
          caretColor: caretHidden ? 'transparent' : undefined,
          ...flatStyle,
          ...(placeholderTextColor
            ? ({
                '--placeholder-color': placeholderTextColor,
              } as CSSProperties)
            : {}),
        }}
        onFocus={() => {
          if (selectTextOnFocus) inputRef.current?.select();
          onFocus?.();
        }}
        onBlur={() => {
          onBlur?.();
          onEndEditing?.(inputRef.current?.value ?? '');
        }}
        onChange={(e) => {
          const converted = toPersianDigits(e.target.value);
          e.target.value = converted;
          onChangeText?.(converted);
        }}
        onKeyDown={(e) => {
          if (e.key === 'Backspace' || e.key === 'Enter') {
            onKeyPress?.(e.key);
          }
          if (e.key === 'Enter') {
            onSubmitEditing?.();
            onEndEditing?.(inputRef.current?.value ?? '');
          }
        }}
      />
    );
  }
);

export default PersianNumberInput;
