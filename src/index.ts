// Reexport the native module. On web, it will be resolved to PersianNumberInputModule.web.ts
// and on native platforms to PersianNumberInputModule.ts
export { default } from './PersianNumberInputModule';
export { default as PersianNumberInputView } from './PersianNumberInputView';
export * from './PersianNumberInput.types';
