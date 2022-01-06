<template>
  <div class="switch_wrapper">
    <label class="switch">
      <input type="checkbox" v-model="internalModel">
      <span class="slider round"></span>
    </label>
    <span class="switch_text"><slot/></span>
  </div>
</template>

<script>
export default {
  name: "ToggleSwitch",
  props: ['value'],
  data() {
    return {
      internalModel: this.value
    }
  },
  watch: {
    value() {
      this.internalModel = this.value;
    },
    internalModel() {
      this.$emit('input', this.internalModel);
    }
  }
}
</script>

<style scoped lang="scss">
  .switch_wrapper {
    display: inline-flex;
    align-items: center;
  }

  .switch_text {
    font-size: 24px;
    text-align: left;
  }

  .switch {
    position: relative;
    display: inline-block;
    margin-right: 10px;
    min-width: 60px;
    width: 60px;
    height: 34px;

    input {
      opacity: 0;
      width: 0;
      height: 0;
    }
  }

  .slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--background);
    transition: var(--default-transition), .4s;
    border-radius: 50px;
    border: 2px solid var(--stroke);

    &:before {
      top: 0;
      bottom: 0;
      margin: auto;
      position: absolute;
      content: "";
      height: 26px;
      width: 26px;
      left: 2px;
      border-radius: 50%;
      background-color: var(--fill);
      transition: var(--default-transition), .4s;
    }
  }

  input:checked + .slider {
    background-color: var(--fill);

    &:before {
      background-color: var(--stroke);
    }
  }

  input:checked + .slider:before {
    transform: translateX(26px);
  }
</style>
