import { NativeModule, requireNativeModule } from 'expo';

import { PersianNumberInputModuleEvents } from './PersianNumberInput.types';

declare class PersianNumberInputModule extends NativeModule<PersianNumberInputModuleEvents> {}

export default requireNativeModule<PersianNumberInputModule>('PersianNumberInput');
