# vue-plock

## Installation

```shell
npm install vue3-plock

yarn add vue3-plock

pnpm add vue3-plock
```

## Usage

`vue3-plock` 导出了一个 `waterFall`组件，接收一个`data`数据以及一个`config`配置

```javascript
<script setup lang="ts">
import { waterFall } from 'vue3-plock';
import { datasource } from './assets/datasource';
</script>

<template>
  <waterFall
    :data="datasource"
    :config="{
      columns: [1, 2, 3],
      gap: [24, 12, 6],
      media: [640, 1024, 1280]
    }"
    v-slot="slotProps"
  >
    <img :src="slotProps.src" :style="{ width: '100%', height: 'auto' }" />
  </waterFall>
</template>

```

[codesandbox](https://codesandbox.io/p/sandbox/compassionate-ace-tfg4dh?file=%2Fsrc%2Fassets%2Fmain.css%3A4%2C21)

## API Reference

#### 数据源

```
:data=[]
```

| Parameter | Type       | Description              |
| :-------- | :--------- | :----------------------- |
| `data`    | `string[]` | **Required**. 图片的 url |

#### 配置项

```
:config={
  columns: [],
  gap: [],
  media: []
}
```

| Parameter | Type       | Description        |
| :-------- | :--------- | :----------------- |
| `config`  | `object[]` | **Required**. 配置 |

- media: 配置响应式断点的阈值
- columns: 设置对应响应区间的列数
- gap: 设置对应响应区间行列的间距

😀**_注意：media、columns、gap 的数量需要保持一致_**
