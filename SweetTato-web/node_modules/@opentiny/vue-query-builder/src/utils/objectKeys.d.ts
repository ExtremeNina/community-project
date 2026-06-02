/**
 * A typed proxy for `Object.keys`
 */
export declare const objectKeys: <T extends Record<string, any>>(obj: T) => (keyof T)[];
