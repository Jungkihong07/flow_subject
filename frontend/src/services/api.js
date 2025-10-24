const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://subjectflow.store/api';

// 고정 확장자 관련 API
export const getFixedExtensions = async () => {
  const response = await fetch(`${API_BASE_URL}/extensions/fixed`);
  if (!response.ok) {
    throw new Error('Failed to fetch fixed extensions');
  }
  return response.json();
};

export const updateFixedExtension = async (name, status) => {
  const response = await fetch(`${API_BASE_URL}/extensions/fixed/${name}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  });
  if (!response.ok) {
    throw new Error('Failed to update fixed extension');
  }
  return response.json();
};

// 커스텀 확장자 관련 API
export const getCustomExtensions = async () => {
  const response = await fetch(`${API_BASE_URL}/extensions/custom`);
  if (!response.ok) {
    throw new Error('Failed to fetch custom extensions');
  }
  return response.json();
};

export const addCustomExtension = async (name) => {
  const response = await fetch(`${API_BASE_URL}/extensions/custom`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name }),
  });
  if (!response.ok) {
    throw new Error('Failed to add custom extension');
  }
  return response.json();
};

export const deleteCustomExtension = async (name) => {
  const response = await fetch(`${API_BASE_URL}/extensions/custom/${name}`, {
    method: 'DELETE',
  });
  if (!response.ok) {
    throw new Error('Failed to delete custom extension');
  }
};

