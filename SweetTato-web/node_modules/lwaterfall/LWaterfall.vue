<style scoped>
.waterfall {
  display: flex;
  justify-content: center;
}
</style>

<template>
  <div id="waterfall" ref="waterfall" class="waterfall">
    <slot>
    </slot>
  </div>
</template>

<script>
import {onMounted, onUpdated, ref, toRefs} from 'vue'
export default {
  name: "LWaterfall",
  props: {
    cols: {
      type: Number,
      default: 2
    },
    colWidth: {
      type: Number,
      default: 0
    },

  },
  setup: function (props) {
    const {cols, colWidth} = toRefs(props)
    let col = 0
    let isWidth = false
    let divs = {}
    function init(waterfallel) {
      divs = {}
      if (colWidth.value == null || colWidth.value == undefined || colWidth.value == "") {
        col = cols.value
      } else {
        isWidth = true
        //获取窗口宽度
        let windowWidth = window.innerWidth;
        //计算列数
        col = Math.floor(windowWidth / colWidth.value);
      }

      for (let i = 0; i < col; i++) {
        let item = document.createElement("div")
        item.className = 'item'
        if (isWidth) {
          item.style.width = colWidth.value + "px"
        } else {
          item.style.flex = 1
        }
        divs[i] = {
          height: 0,
          div: item
        }
      }

      let children = waterfallel.value.children
      while (children.length > 0) {
        let child = children[0]
        child.style.width = 'auto'
        let minIndex = findMinHeight()
        divs[minIndex].height += child.clientHeight
        divs[minIndex].div.appendChild(child)
      }
      //  添加到dom中
      for (let i = 0; i < col; i++) {
        waterfallel.value.appendChild(divs[i].div)
      }
    }

    //寻找最短列
    function findMinHeight() {
      let minHeight = 99999999;
      let minIndex = 0;
      for (let i = 0; i < col; i++) {
        let item = divs[i]
        if (item.height < minHeight) {
          minHeight = item.height
          minIndex = i
        }
      }
      return minIndex
    }

    const waterfall = ref(null)

    onUpdated(() => {
      while (waterfall.value.children.length > 0) {
        let child = waterfall.value.children[0]
        if (child.className == 'item') {
          waterfall.value.removeChild(child)
        } else {
          child.style.width = 'auto'
          let minIndex = findMinHeight()
          divs[minIndex].height += child.clientHeight
          divs[minIndex].div.appendChild(child)
        }
      }

      for (let i = 0; i < col; i++) {
        waterfall.value.appendChild(divs[i].div)
      }
    })

    onMounted(() => {
      init(waterfall)
    })

    return {
      waterfall,
      init,
    }
  },


}
</script>

