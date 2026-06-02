import { BadgeOptions, CallBackOptions, ConnectOptions, GoEasyOptions, MessageCallback, Scene } from "./InterfaceTypes";
import { GWS } from "./pubsub/GWS";
import { GIM } from "./im/GIM";
import { ImApiEvents } from "./im/events/api/im-api-events";
export default class GoEasy {
    static instance: GoEasy;
    static readonly version: string;
    static readonly IM_EVENT: typeof ImApiEvents;
    static readonly IM_SCENE: typeof Scene;
    static im: typeof GIM;
    static pubsub: typeof GWS;
    private static options;
    static getInstance(options: GoEasyOptions): GoEasy;
    static init(options: GoEasyOptions): void;
    static setBadge(options: BadgeOptions): void;
    static connect(connectOptions: ConnectOptions): void;
    static disconnect(options: CallBackOptions): void;
    static getConnectionStatus(): string;
    private static validateOptions;
    static onClickNotification(clickHandler: MessageCallback): void;
    static c(obj: any): void;
}
