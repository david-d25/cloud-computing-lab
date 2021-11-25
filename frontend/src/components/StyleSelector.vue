<template>
  <div class="style_selector">
    <div class="title">Выберите стили</div>
    <div class="styles">
<!--      TODO-->
    </div>
  </div>
</template>

<script>
export default {
  name: "StyleSelector",
  props: {
    styles: Array
  }
}
</script>

<style scoped>
  .title {
    font-size: 24px;
    margin-bottom: 17px;
  }
</style>
