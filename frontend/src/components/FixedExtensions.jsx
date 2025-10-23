import React, { useState, useEffect } from 'react';
import { getFixedExtensions, updateFixedExtension } from '../services/api';

const FixedExtensions = () => {
  const [extensions, setExtensions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadExtensions();
  }, []);

  const loadExtensions = async () => {
    try {
      const data = await getFixedExtensions();
      setExtensions(data);
    } catch (error) {
      console.error('Failed to load fixed extensions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = async (name, currentStatus) => {
    const newStatus = currentStatus === 'CHECKED' ? 'UNCHECKED' : 'CHECKED';
    
    // Optimistic update
    setExtensions(prev => 
      prev.map(ext => 
        ext.name === name ? { ...ext, status: newStatus } : ext
      )
    );

    try {
      await updateFixedExtension(name, newStatus);
    } catch (error) {
      console.error('Failed to update extension:', error);
      // Rollback on error
      setExtensions(prev => 
        prev.map(ext => 
          ext.name === name ? { ...ext, status: currentStatus } : ext
        )
      );
    }
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div>
      <h2 className="section-title">고정 확장자</h2>
      <div className="fixed-extensions-container">
        {extensions.map(extension => (
          <div key={extension.name} className="extension-checkbox">
            <input
              type="checkbox"
              id={`fixed-${extension.name}`}
              checked={extension.status === 'CHECKED'}
              onChange={() => handleToggle(extension.name, extension.status)}
            />
            <label htmlFor={`fixed-${extension.name}`}>
              {extension.name}
            </label>
          </div>
        ))}
      </div>
    </div>
  );
};

export default FixedExtensions;

