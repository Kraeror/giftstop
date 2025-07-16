<template>
  <div class="relative w-full max-w-md">
    <div class="mb-2">
      <button @click="isOpen = !isOpen" type="button" class="w-full px-4 py-2 border rounded bg-white text-left shadow focus:outline-none">
        {{ selectedCategoryObjects.length === 0 ? 'Избери категория' : 'Добави още категории' }}
        <span class="float-right">
          <i :class="{'fa-chevron-up': isOpen, 'fa-chevron-down': !isOpen }" class="fa ml-2"></i>
        </span>
      </button>

      <div v-if="isOpen" class="absolute z-10 w-full mt-1 border rounded bg-white shadow max-h-96 overflow-y-auto">
        <div v-if="breadcrumbs.length" class="text-sm px-4 py-2 text-gray-500 border-b bg-gray-50">
          <span class="cursor-pointer hover:underline" @click="goBackTo(0)">Начало</span>
          <span v-for="(crumb, i) in breadcrumbs" :key="i">
            › <span class="cursor-pointer hover:underline" @click="goBackTo(i + 1)">{{ crumb.name }}</span>
          </span>
        </div>

        <div
          v-for="cat in currentCategories"
          :key="cat.id"
          class="flex items-center hover:bg-pink-50 cursor-pointer divide-x divide-gray-200"
        >
          <!-- Left 80%: Select category -->
          <div
            class="flex-1 px-4 py-2 text-gray-700"
            @click.stop="addCategory(cat)"
          >
            {{ cat.name }}
          </div>

          <!-- Right 20%: Enter subcategories -->
          <div
            class="px-3 py-2 w-10 text-right"
            v-if="cat.children && cat.children.length"
          >
            <button @click.stop="enterCategory(cat)" type="button" class="text-gray-500">
              <i class="fa fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Избраните категории -->
    <div class="flex flex-wrap gap-2">
      <div v-for="(cat, index) in selectedCategoryObjects" :key="cat.id" class="bg-pink-100 text-pink-800 px-3 py-1 rounded-full flex items-center">
        <span>{{ cat.name }}</span>
        <button @click.stop="removeCategory(index)" class="ml-2 text-pink-600 hover:text-pink-800">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
// @vue/compiler-sfc <script setup> macros
/* eslint-disable no-undef */
import { ref, computed, watch } from 'vue';

const props = defineProps({
  categories: Array,
  selectedCategories: Array
})
const emit = defineEmits(['select'])

const isOpen = ref(false);
const breadcrumbs = ref([]);
const selectedCategoryObjects = ref([]);

const currentCategories = computed(() => {
  return breadcrumbs.value.length === 0
    ? props.categories
    : breadcrumbs.value[breadcrumbs.value.length - 1].children || [];
})

watch(
  () => [props.categories, props.selectedCategories],
  ([cats, ids]) => {
    if (cats?.length && ids?.length) {
      selectedCategoryObjects.value = ids
        .map(id => findCategoryById(cats, id))
        .filter(Boolean);
    }
  },
  { immediate: true }
);

function findCategoryById(categories, id) {
  for (const cat of categories) {
    if (cat.id === id) return cat;
    if (cat.children?.length) {
      const found = findCategoryById(cat.children, id);
      if (found) return found;
    }
  }
  return null;
}

const enterCategory = (cat) => {
  breadcrumbs.value.push(cat);
}

const goBackTo = (index) => {
  breadcrumbs.value = breadcrumbs.value.slice(0, index);
}

const addCategory = (cat) => {
  if (!selectedCategoryObjects.value.some(c => c.id === cat.id)) {
    selectedCategoryObjects.value.push(cat);
    emit('select', cat.id);
  }
  isOpen.value = false;
}

const removeCategory = (index) => {
  const removed = selectedCategoryObjects.value.splice(index, 1)[0];
  emit('unselect', removed.id);
}
</script>

<style scoped>
.fa {
  font-size: 0.75rem;
}
</style>
