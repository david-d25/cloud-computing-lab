<template>
  <div class="tabbed_view_wr">
    <div class="tabs">
      <label class="tab_wrapper" v-for="tab in tabs" :key="tab.id">
        <input class="tab_radio" type="radio" name="tab" :value="tab.id" v-model="currentTab">
        <span class="tab">{{ tab.name }}</span>
      </label>
    </div>
    <div class="view" v-for="tab in tabs" :key="tab.id" v-if="tab.id === currentTab">
      <slot :name="tab.id"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "TabbedView",
  props: {
    tabs: Array
  },
  data() {
    return {
      currentTab: this.tabs.filter(it => it['default'] === true)[0]['id']
    }
  }
}
</script>

<style scoped lang="scss">
  .tab_wrapper {
    margin-right: 16px;

    &:last-child {
      margin-right: 0;
    }
  }

  .tab_radio {
    opacity: 0;
    width: 0;
    height: 0;
  }

  .tab_radio:checked + .tab {
    background: var(--fill);
  }

  .tab_radio:focus + .tab {
    outline: 2px solid var(--stroke);
  }

  .tabs {
    margin-bottom: 28px;
  }

  .tab {
    display: inline-block;
    padding: 16px 32px;
    background: var(--light-grey);
    font-size: 18px;
    border-radius: 100px;
  }
</style>
