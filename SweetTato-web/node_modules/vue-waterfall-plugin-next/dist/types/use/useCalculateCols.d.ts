import type { WaterfallProps } from '../types/waterfall';
export declare function useCalculateCols(props: WaterfallProps): {
    waterfallWrapper: any;
    wrapperWidth: import("vue").Ref<number>;
    colWidth: import("vue").ComputedRef<number>;
    cols: import("vue").ComputedRef<number>;
    offsetX: import("vue").ComputedRef<number>;
};
