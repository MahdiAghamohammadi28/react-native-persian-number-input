import type { StyleProp, ViewStyle } from 'react-native';

export type PersianNumberInputModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
};

export type ChangeEventPayload = {
  value: string;
};

export type OnTapEventPayload = Record<string, never>;

export type PersianNumberInputViewProps = {
  style?: StyleProp<ViewStyle>;
};
