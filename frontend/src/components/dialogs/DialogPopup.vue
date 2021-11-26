<template>
  <div class="dialog_wrapper">
    <transition name="fade">
      <div class="dialog_shadow_overlay" v-if="show" @click="$emit('close')"></div>
    </transition>
    <transition name="slide">
      <div class="dialog_content" v-if="show">
        <slot/>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "DialogPopup",
  props: ['show']
}
</script>

<style scoped lang="scss">
  .dialog_wrapper {
    left: 0;
    top: 0;
    width: 0;
    height: 0;
    position: fixed;
    z-index: 99999999999;
  }

  .dialog_shadow_overlay {
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, .6);
    transition: var(--default-transition), opacity 500ms linear;

    &.fade-enter-active {
      transition: var(--default-transition), opacity 200ms ease-out;
    }

    &.fade-enter, &.fade-leave-to {
      opacity: 0;
    }
  }

  .dialog_content {
    bottom: 0;
    width: calc(100vw - (100vw - 100%)); // Workaround for scrollbar
    max-width: 600px;
    left: 0;
    right: 0;
    margin: auto;
    padding: 25px;
    min-height: 25vh;
    max-height: 100vh;
    overflow: auto;
    position: fixed;
    border-radius: 25px 25px 0 0;
    background: var(--background);
    transition: var(--default-transition), transform 300ms cubic-bezier(.31,.44,.42,.98);

    &.slide-enter, &.slide-leave-to {
      transform: translateY(100%);
    }
  }
</style>
