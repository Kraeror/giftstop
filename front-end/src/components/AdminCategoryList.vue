<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <h1 class="text-2xl font-bold mb-6 text-pink-600">Категории</h1>

    <div
      v-for="category in categories"
      :key="category.id || category.tempId"
      class="mb-2 p-3 bg-white rounded shadow"
    >
      <AdminCategoryNode
        :category="category"
        @add-child="addCategory"
        @refresh="fetchCategories"
        @update-name="updateCategoryName"
        @remove-temp="removeTemp"
        @delete-category="deleteCategory"
      />
    </div>

    <button
      class="mt-6 bg-pink-500 text-white px-4 py-2 rounded hover:bg-pink-600"
      @click="addCategory(null, true)"
    >
      + Нова основна категория
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import AdminCategoryNode from '@/components/AdminCategoryNode.vue';

const categories = ref([]);
let tempIdCounter = -1;

async function fetchCategories() {
  try {
    const res = await axios.get('/api/admin/categories');
    categories.value = res.data;
  } catch (e) {
    console.error('Грешка при зареждане на категории:', e);
  }
}

function addCategory(parentId, isRoot = false) {
  const newCategory = {
    name: '',
    parentId,
    tempId: tempIdCounter--,
    children: [],
    isNew: true,
  };

  if (isRoot) {
    categories.value.push(newCategory);
  } else {
    const parent = findCategoryById(categories.value, parentId);
    if (parent) {
      parent.children = parent.children || [];
      parent.children.push(newCategory);
    }
  }
}

async function updateCategoryName({ id, name }) {
  if (!name || name.trim() === '') return;

  try {
    if (id < 0) {
      const parentId = findParentId(categories.value, id);
      removeCategoryByTempId(id);
      const res = await axios.post('/api/admin/categories', { name, parentId });
      insertRealCategory(res.data, parentId);
    } else {
      await axios.patch(`/api/admin/categories/${id}`, { name });
      const cat = findCategoryById(categories.value, id);
      if (cat) cat.name = name;
    }
  } catch (e) {
    console.error('Грешка при запазване на име на категория:', e);
  }
}

function insertRealCategory(category, parentId) {
  if (parentId == null) {
    categories.value.push({ ...category, children: [] });
  } else {
    const parent = findCategoryById(categories.value, parentId);
    if (parent) {
      parent.children = parent.children || [];
      parent.children.push({ ...category, children: [] });
    }
  }
}

function findCategoryById(list, id) {
  for (const cat of list) {
    if (cat.id === id || cat.tempId === id) return cat;
    if (cat.children) {
      const found = findCategoryById(cat.children, id);
      if (found) return found;
    }
  }
  return null;
}

function findParentId(list, childTempId, parentId = null) {
  for (const cat of list) {
    if (cat.tempId === childTempId) return parentId;
    if (cat.children) {
      const found = findParentId(cat.children, childTempId, cat.id || cat.tempId);
      if (found !== null) return found;
    }
  }
  return null;
}

function removeCategoryByTempId(tempId) {
  const remove = (list) => {
    for (let i = list.length - 1; i >= 0; i--) {
      if (list[i].tempId === tempId) {
        list.splice(i, 1);
        return true;
      } else if (list[i].children) {
        if (remove(list[i].children)) return true;
      }
    }
    return false;
  };
  remove(categories.value);
}

async function deleteCategory(id) {
  try {
    await axios.delete(`/api/admin/categories/${id}`);
    fetchCategories();
  } catch (e) {
    console.error('Грешка при изтриване на категория:', e);
  }
}

function removeTemp(tempId) {
  const removeRecursively = (list) => {
    return list.filter(cat => {
      if (cat.tempId === tempId) return false;
      if (cat.children) {
        cat.children = removeRecursively(cat.children);
      }
      return true;
    });
  };
  categories.value = removeRecursively(categories.value);
}

onMounted(fetchCategories);
</script>

<style scoped>
/* Можеш да добавиш допълнителни стилове тук при нужда */
</style>
