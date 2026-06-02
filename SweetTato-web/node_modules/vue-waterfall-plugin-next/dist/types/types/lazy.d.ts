import type Lazy from '../utils/Lazy';
export declare type CallbackFunction = (value: boolean) => void;
export interface LazyOptions {
    error?: string;
    loading?: string;
    observerOptions?: IntersectionObserverInit;
    log?: boolean;
    ratioCalculator?: (width: number, height: number) => number;
}
export interface ValueFormatterObject {
    src: string;
    error?: string;
    loading?: string;
}
export declare type LazyType = InstanceType<typeof Lazy>;
