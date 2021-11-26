<template>
  <div class="message_panel">
    <slot></slot>
  </div>
</template>

<script>
export default {
  props: ['red', 'green', 'blue']
}
</script>

<style scoped>
.message_panel {
  border-width: 1px;
  border-style: solid;
  border-color: #2b2b2b;
  background: rgba(255, 255, 255, .2);
  padding: 10px;
}
</style>
