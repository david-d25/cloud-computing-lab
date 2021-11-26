<template>
  <div class="select_wr" :class="{ expanded }" v-click-outside="() => this.expanded = false">
    <div class="select_visual"
         tabindex="0"
         :data-state-wrong="errorHint != null"
         @click="expanded = !expanded"
         @keypress.enter="expanded = !expanded">
      <div class="title" v-if="value">{{ options.filter(o => o.value === value)[0].title }}</div>
      <div class="title placeholder" :class="{ active: expanded || value }">{{ placeholder || '' }}</div>
      <div class="reset_btn" v-show="value" @click.stop="localValue = null"></div>
      <div class="options" v-if="expanded">
        <div class="option" v-for="option in options" :key="option.value" @click="onSelect(option.value)">{{ option.title }}</div>
      </div>
    </div>
    <transition-expand>
      <div class="err_msg" v-if="errorHint != null">
        {{ errorHint }}
      </div>
    </transition-expand>
  </div>
</template>

<script>
import ClickOutsideDirective from '#/directives/click_outside.js';
import TransitionExpand from "#/components/TransitionExpand";

export default {
  components: {TransitionExpand},
  props: ['value', 'options', 'placeholder', 'errorHint'],
  data() {
    return {
      localValue: this.value === undefined ? null : this.value,
      expanded: false
    }
  },
  directives: {
    'click-outside': ClickOutsideDirective
  },
  methods: {
    onSelect(value) {
      this.localValue = value;
      this.expanded = false;
    },
    onFocus() {
      if (!this.value)
        this.expanded = true;
    }
  },
  watch: {
    value() {
      this.localValue = this.value === undefined ? null : this.value;
    },
    localValue() {
      this.$emit('input', this.localValue);
    }
  }
}
</script>

<style scoped lang="scss">
  .select_wr {
    font-size: 22px;
    line-height: 22px;
    position: relative;
  }

  .select_visual {
    padding: 10px;
    border: 1px solid var(--grey);
    background: var(--background);
    min-height: 22px;
    box-sizing: content-box;
    border-radius: 4px;
    position: relative;
  }

  .title {
    text-align: left;
  }

  .placeholder {
    color: var(--grey);
    font-size: 0.8em;
    position: absolute;
    padding: 0 5px;
    background: var(--background);
    transition: var(--default-transition), top 300ms ease-in-out;
    left: 10px;
    top: 25%;

    &.active {
      top: -25%;
      color: var(--dark-grey);
    }
  }

  .reset_btn {
    position: absolute;
    margin: auto;
    right: 0;
    bottom: 0;
    top: 0;
    width: 50px;
    min-height: 100%;
    overflow: hidden;

    &:before, &:after {
      display: block;
      content: '';
      border-top: 4px solid var(--dark-grey);
      width: 20px;
      margin: auto;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      transform: rotate(45deg) translateY(50%) translateY(-2px);
      position: absolute;
    }

    &:after {
      transform: rotate(-45deg) translateY(50%) translateY(-2px);
    }
  }

  .options {
    position: absolute;
    max-height: 200px;
    overflow: auto;
    background: var(--background);
    outline: 1px solid var(--grey);
    border-radius: 0 0 4px 4px;
    border-top: none;
    z-index: 999999999;
    width: 100%;
    top: 100%;
    left: 0;
  }

  .option {
    padding: 15px 10px;
    text-align: left;
    transition: none;
    cursor: default;

    &:hover {
      background: var(--grey);
    }
  }

  .expanded {
    .select_visual {
      border-radius: 4px 4px 0 0;
    }
  }

  [data-state-wrong='true'] {
    border: 1px solid rgba(255, 0, 0, .5);
    box-shadow: 0 0 2px red;
  }

  .err_msg {
    color: darkred;
    font-size: 16px;
  }
</style>
