# lwaterfall-vue3
* 1.完全由您控制，此组件只将您的元素转换为瀑布流形式
* 2.使用极为简便,适用于PC/ios/android
* 3.此组件为vue3版本


有问题欢迎提issues、suggestions;感谢您的star !
更希望大家能提出bug，或将您的修改提交，让这个组件更加完善。感谢！！

## 安装
```
npm  i lwaterfall
``` 
## 引入
```
 import LWaterfall from 'LWaterfall'
```

## <LWaterfall> 属性
名称 | 默认值 | 类型  | 说明                                 | 必填
-------- |-----|-----|------------------------------------| ------
cols | 2  | Number | 列数                                 | 否
colWidth | - | Number | 列宽(配置此项cols将失效)                    | 否


## <LWaterfall> 示例
### dom部分
```haml
  <l-waterfall v-if="data" :cols="2" >
            <div class="div" style="height:50px" v-for="(item,index) in data" :key="index">
             <!--            <div class="div" style="height:50px" v-for="(item,index) in 10" :key="index">-->
              {{ item }}
            </div>
    </l-waterfall>
    <div @click="b">添加数据</div>
```
### js部分
```js
//方便演示增加数据
data() {
    return {
      data: [1,2,3,4,5,6,7,8,9,10]
    }
  },
  methods: {
    b() {
      this.data = [...this.data, ...this.data]
    }
  },
```



林轻影
